package com.abc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abc.dao.FollowDAO;
import com.abc.entities.User;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowDAO followDAO;

    @Override
    public List<User> getFollowerUser(int id) {
        return followDAO.getFollowerUser(id);
    }

    @Override
    public List<User> getFollowedUsers(int id) {
        return followDAO.getFollowedUsers(id);
    }

    @Override
    public void followUser(int followingUserId, int followedUserId) {
        followDAO.followUser(followingUserId, followedUserId);
    }

    @Override
    public void unfollowUser(int followingUserId, int followedUserId) {
        followDAO.unfollowUser(followingUserId, followedUserId);
    }

    @Override
    public List<User> getSuggestedFollows(int userId) {
        return followDAO.getSuggestedFollows(userId);
    }

    @Override
    public List<User> searchUsersByFollowCounts(int minFollowing, int minFollower) {
        return followDAO.searchUsersByFollowCounts(minFollowing, minFollower);
    }
}