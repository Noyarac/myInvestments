package eu.carayon.myInvestments.dal;

public class DaoFactory {

    @SuppressWarnings("unchecked")
    public static <T> Dao<T> getDao(Class<T> clazz) {
        DaoProvider provider = DaoProvider.Json;
        try {
            String packageName = "eu.carayon.myInvestments.dal.impl";
            String className = packageName + "." + clazz.getSimpleName() + "Dao" + provider;
            Class<?> daoClass = Class.forName(className);
            if (!Dao.class.isAssignableFrom(daoClass)) {
                throw new IllegalArgumentException(className + " n'implémente pas l'interface Dao");
            }
            return (Dao<T>) daoClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Aucune implémentation trouvée pour " + clazz.getSimpleName() + " avec le fournisseur " + provider, e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'instanciation du DAO pour " + clazz.getSimpleName() + " avec le fournisseur " + provider, e);
        }
    }
}