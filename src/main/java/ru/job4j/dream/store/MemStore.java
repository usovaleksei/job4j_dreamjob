package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemStore implements Store {

    private static final MemStore INST = new MemStore();

    private static final AtomicInteger POST_ID = new AtomicInteger(4);
    private static final AtomicInteger CAND_ID = new AtomicInteger(4);

    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();
    private final Map<String, User> users = new ConcurrentHashMap<>();

    private MemStore() {
    }

    public static MemStore instOf() {
        return INST;
    }

    public Collection<Post> findAllPosts() {
        return this.posts.values();
    }

    @Override
    public void createCandidate(Candidate candidate) {
        candidate.setId(CAND_ID.incrementAndGet());
        this.candidates.put(candidate.getId(), candidate);
    }

    @Override
    public void updateCandidate(Candidate candidate) {
        this.candidates.replace(candidate.getId(), candidate);

    }

    @Override
    public void saveCandidate(Candidate candidate) {
        if (candidate.getId() == 0) {
            createCandidate(candidate);
        } else {
            updateCandidate(candidate);
        }
    }

    public Collection<Candidate> findAllCandidates() {
        return this.candidates.values();
    }

    @Override
    public void createPost(Post post) {
        post.setId(POST_ID.incrementAndGet());
        this.posts.put(post.getId(), post);

    }

    @Override
    public void updatePost(Post post) {
        this.posts.replace(post.getId(), post);
    }

    @Override
    public void savePost(Post post) {
        if (post.getId() == 0) {
            createPost(post);
        } else {
            updatePost(post);
        }
    }

    public Post findPostById(int id) {
        return this.posts.get(id);
    }

    public Candidate findCandidateById(int id) {
        return this.candidates.get(id);
    }

    public void deleteCandidateById(int id) {
        this.candidates.remove(id);
    }

    @Override
    public void createUser(User user) {
        if (user.getId() == 0) {
            user.setId(POST_ID.incrementAndGet());
        }
        this.users.put(user.getEmail(), user);

    }

    @Override
    public User findUserByEmail(String email) {
        return this.users.get(email);
    }
}
