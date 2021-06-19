package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.util.Collection;

public interface Store {

    void createPost(Post post);

    void updatePost(Post post);

    void savePost(Post post);

    Post findPostById(int id);

    Collection<Post> findAllPosts();

    void createCandidate(Candidate candidate);

    void updateCandidate(Candidate candidate);

    void saveCandidate(Candidate candidate);

    Candidate findCandidateById(int id);

    Collection<Candidate> findAllCandidates();

    void deleteCandidateById(int id);

    void createUser(User user);

    User findUserByEmail(String email);

    void updateUser(User user);

    void deleteUserByEmail(String email);
}
