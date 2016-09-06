package JFiles.dao;

import JFiles.model.UserEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**Responsible for interaction with <i>User</i> database<br>
 * Methods are self-explanatory*/
@Repository
public class UserDAOimpl implements UserDAO {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public UserEntity getUserByName(String name) {

        Session session = sessionFactory.getCurrentSession();

        return (UserEntity) session.get(UserEntity.class, name);
    }

    public void addUser(UserEntity user) {

        Session session = sessionFactory.getCurrentSession();

        session.persist(user);
    }

    public void remove(String user) {

        Session session = sessionFactory.getCurrentSession();
        UserEntity ue = (UserEntity)session.get(UserEntity.class, user);
        session.delete(ue);
    }

    public void update(UserEntity user) {

        Session session = sessionFactory.getCurrentSession();
        session.update(user);
    }

    public List<UserEntity> getAllUsers() {

        Session session = sessionFactory.getCurrentSession();

        List<UserEntity> list = session.createQuery("FROM UserEntity ").list();

        return list;
    }

    @Override
    public List<String> getRecordsWithEmail(String email) {

        Session session = sessionFactory.getCurrentSession();

        List<String> emails = session.createQuery("SELECT U.email FROM UserEntity U WHERE U.email = '" + email + "'").list();

        return emails;
    }
}
