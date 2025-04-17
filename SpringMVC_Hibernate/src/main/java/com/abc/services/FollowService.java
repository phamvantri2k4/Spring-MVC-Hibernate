package com.abc.services;

import java.util.List;

import com.abc.entities.User;

public interface FollowService {

    List<User> getFollowerUser(int id);

    List<User> getFollowedUsers(int id);

    void followUser(int followingUserId, int followedUserId);

    void unfollowUser(int followingUserId, int followedUserId);

    List<User> getSuggestedFollows(int userId);

    List<User> searchUsersByFollowCounts(int minFollowing, int minFollower);
}