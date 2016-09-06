package JFiles.dao;

import JFiles.model.StatisticEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**Responsible for interaction with <i>Statistic</i> database<br>
 * Methods are self-explanatory*/
@Repository
public class StatisticDAO {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public StatisticEntity getStatisticEntryById(int id) {

        Session session = sessionFactory.getCurrentSession();

        return (StatisticEntity) session.get(StatisticEntity.class, id);
    }

    public StatisticEntity getRecordByUserName(String userName){

        Session session = sessionFactory.getCurrentSession();

        List<StatisticEntity> list = session.createQuery("SELECT R FROM StatisticEntity R WHERE R.user = '" + userName+ "'").list();

        if( list.size() > 0){

            return list.get(0);
        }
        else {
            return null;
        }
    }

    /**After game end there is need to update exact combination of User-VsUser<br>
     * Normally only one pair exist but during development fake records have been added and list could contain more than 1 record*/
    public StatisticEntity getRecordByUserNames(String userName, String vsUserName){

        Session session = sessionFactory.getCurrentSession();

        List<StatisticEntity> list = session.createQuery(
                "SELECT R FROM StatisticEntity R WHERE R.user = '" + userName+ "' AND R.vsUser = '" + vsUserName +"'" ).list();

        if( list.size() > 0){
            return list.get(0);
        }
        else {
            return null;
        }
    }

    public List<StatisticEntity> getAllRecords() {

        Session session = sessionFactory.getCurrentSession();

        List<StatisticEntity> list = session.createQuery("FROM StatisticEntity ").list();

        return list;
    }

    public List<StatisticEntity> getAllRecordsWithUser(String user) {

        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("SELECT R FROM StatisticEntity R WHERE R.user = '" + user + "'").list();
    }

    public void addRecord(StatisticEntity record) {

        Session session = sessionFactory.getCurrentSession();

        session.persist(record);
    }

    public void update(StatisticEntity record) {

        Session session= sessionFactory.getCurrentSession();
        session.update(record);
    }

    public void deleteRecord(int id) {

        Session session = sessionFactory.getCurrentSession();

        StatisticEntity record = (StatisticEntity)session.get(StatisticEntity.class, id);

        session.delete(record);
    }

}
