package com.abc.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.abc.entities.User;

@Repository
@Transactional
public class UserDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public User getUserByUserName(String userName) {
        String hql = "FROM User u WHERE u.username = :username";
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery(hql, User.class);
        query.setParameter("username", userName);
        return query.uniqueResult();
    }

    public boolean registerUser(User user) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkEmailExists(String email) {
        String hql = "FROM User u WHERE u.email = :email";
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery(hql, User.class);
        query.setParameter("email", email);
        return query.uniqueResult() != null;
    }

    public List<User> searchUsersByFollowCounts(int minFollowing, int minFollower) {
        String hql = "SELECT u FROM User u " +
                     "WHERE (SELECT count(f) FROM Follow f WHERE f.id.followingUserId = u.id) >= :minFollowing " +
                     "   OR (SELECT count(f) FROM Follow f WHERE f.id.followedUserId = u.id) >= :minFollower";
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery(hql, User.class);
        query.setParameter("minFollowing", (long) minFollowing);
        query.setParameter("minFollower", (long) minFollower);
        return query.getResultList();
    }

    public void updateUser(User user) {
        sessionFactory.getCurrentSession().update(user);
    }
}