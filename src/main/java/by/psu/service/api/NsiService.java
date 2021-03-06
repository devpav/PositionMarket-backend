package by.psu.service.api;

import by.psu.merger.AbstractNsiMerger;
import by.psu.model.postgres.BasicEntity;
import by.psu.model.postgres.Language;
import by.psu.model.postgres.Nsi;
import by.psu.model.postgres.StringValue;
import by.psu.model.postgres.repository.RepositoryNsi;
import javassist.tools.web.BadHttpRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public abstract class NsiService<T extends Nsi> extends AbstractService<T> {

    protected RepositoryNsi<T> repositoryNsi;

    private AbstractNsiMerger<T> abstractNsiMerger;

    @PersistenceContext
    protected EntityManager entityManager;

    private Class<T> loggerClass;


    public NsiService(RepositoryNsi<T> repositoryNsi, AbstractNsiMerger<T> abstractNsiMerger, Class<T> loggerClass) {
        super(repositoryNsi, loggerClass);
        this.loggerClass = loggerClass;
        this.repositoryNsi = repositoryNsi;
        this.abstractNsiMerger = abstractNsiMerger;
    }


    @Transactional(readOnly = true)
    public List<T> getAll() {
        return repositoryNsi.findAll();
    }

    @Transactional(readOnly = true)
    public T getOne(UUID uuid) {
        return repositoryNsi.getOne(uuid);
    }

    @Transactional
    protected Optional<T> isExists(T nsi) {

        if (nsi == null) {
            return Optional.empty();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(loggerClass);
        Root<T> root = criteriaQuery.from(loggerClass);

        Subquery<StringValue> stringValueSubquery = criteriaQuery.subquery(StringValue.class);
        Root<StringValue> stringValueRoot = stringValueSubquery.from(StringValue.class);

        Optional<StringValue> stringValueEn = TranslateUtil.getValueByLanguage(nsi.getTitle(), Language.EN);
        Optional<StringValue> stringValueRu = TranslateUtil.getValueByLanguage(nsi.getTitle(), Language.RU);

        List<Predicate> expressions = new ArrayList<>();

        stringValueEn.ifPresent(stringValue -> expressions.add(
                criteriaBuilder.like(criteriaBuilder.lower(stringValueRoot.get("value")), stringValue.getValue().toLowerCase())
        ));

        stringValueRu.ifPresent(stringValue -> expressions.add(
                criteriaBuilder.like(criteriaBuilder.lower(stringValueRoot.get("value")), stringValue.getValue().toLowerCase())
        ));

        if (expressions.isEmpty()) {
            throw new RuntimeException("Nsi doesn't have a valid value");
        }

        Predicate predicate = expressions.size() == 1 ? expressions.get(0) :
                    criteriaBuilder.or(expressions.toArray(new Predicate[0]));

        stringValueSubquery.select(stringValueRoot)
                .where(
                        criteriaBuilder.and(
                                predicate,
                                criteriaBuilder.equal(root.get("title"), stringValueRoot.get("translate"))
                        )
                );

        criteriaQuery.select(root).where(criteriaBuilder.exists(stringValueSubquery));
        TypedQuery<T> tTypedQuery = entityManager.createQuery(criteriaQuery);

        List<T> objList = tTypedQuery.getResultList();
        return objList.stream().findFirst();
    }

    @Transactional
    public T update(T nsi) {
        Optional<T> optionalNsi = Optional.of(nsi);
        optionalNsi.orElseThrow(() -> new RuntimeException("Nsi is null", new BadHttpRequest()));
        optionalNsi.map(Nsi::getId).orElseThrow(() -> new RuntimeException("Id is null"));

        Optional<T> findNsi = Optional.of(repositoryNsi.getOne(nsi.getId()));
        findNsi.orElseThrow(() -> new RuntimeException(new EntityNotFoundException("Nsi not found")));

        optionalNsi.map(Nsi::getTitle).orElseThrow(() -> new RuntimeException(new EntityNotFoundException("Nsi title is null")));
        findNsi.map(Nsi::getTitle).orElseThrow(() -> new RuntimeException(new EntityNotFoundException("Find nsi (BD) title is null")));

        return repositoryNsi.save(abstractNsiMerger.merge(findNsi.get(), nsi));
    }

    @Transactional
    public T save(T nsi) {
        Optional<T> optionalNsi = Optional.ofNullable(nsi);
        optionalNsi.orElseThrow(() -> new RuntimeException("Nsi is null", new BadHttpRequest()));

        if ( optionalNsi.map(Nsi::getId).isPresent() ) {
             return repositoryNsi.getOne(optionalNsi.map(Nsi::getId).get());
        }

        optionalNsi.map(Nsi::getTitle).orElseThrow(() -> new RuntimeException("Nsi title is null", new BadHttpRequest()));

        optionalNsi.get().getTitle().setListValues(optionalNsi.get().getTitle().getValues());

        return isExists(optionalNsi.get()).orElseGet(() -> repositoryNsi.save(optionalNsi.get()));
    }

    public List<T> place(List<T> nsiCollection) {

        if (isNull(nsiCollection)) {
            return new ArrayList<>();
        }

        return nsiCollection.stream()
                .map(nsiItem -> isExists(nsiItem).orElseGet(() -> repositoryNsi.save(nsiItem)))
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID uuid) {
        repositoryNsi.deleteById(uuid);
    }

    @Transactional
    public void deleteAll(final List<UUID> uuid) {
        if ( isNull(uuid) || uuid.isEmpty() ) {
            return;
        }

        List<T> objects = uuid.stream()
                .map(repositoryNsi::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        for (T it : objects) {
            deleteConsumer(it);
            entityManager.merge(it);
            entityManager.flush();
            entityManager.clear();
        }

        objects.stream()
                .map(BasicEntity::getId)
                .forEach(repositoryNsi::deleteById);
    }

    protected List<T> getReferencesIds(List<UUID> uuids) {
        if (isNull(uuids) || uuids.isEmpty() ) {
            return Collections.emptyList();
        }

        return uuids.stream()
                .map(repositoryNsi::getOne)
                .collect(Collectors.toList());
    }

    protected List<T> getReferencesByEntities(List<T> uuids) {
        if (isNull(uuids) || uuids.isEmpty() ) {
            return Collections.emptyList();
        }

        return uuids.stream()
                .map(BasicEntity::getId)
                .map(repositoryNsi::getOne)
                .collect(Collectors.toList());
    }

    @Transactional
    abstract protected void deleteConsumer(T object);

}
