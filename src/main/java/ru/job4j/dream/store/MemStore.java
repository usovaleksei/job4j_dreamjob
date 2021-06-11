package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemStore {

    private static final MemStore INST = new MemStore();

    private static final AtomicInteger POST_ID = new AtomicInteger(4);
    private static final AtomicInteger CAND_ID = new AtomicInteger(4);

    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private MemStore() {
        posts.put(1, new Post(1, "Junior Java Job", "Write code as junior", LocalDateTime.now().minusHours(1)));
        posts.put(2, new Post(2, "Middle Java Job", "Write code as middle", LocalDateTime.now().minusHours(3)));
        posts.put(3, new Post(3, "Senior Java Job", "Write code as senior", LocalDateTime.now().minusHours(5)));
        candidates.put(1, new Candidate(1, "Junior JAva"));
        candidates.put(2, new Candidate(2, "Middle JAva"));
        candidates.put(3, new Candidate(3, "Senior JAva"));
    }

    public static MemStore instOf() {
        return INST;
    }

    public Collection<Post> findAllPosts() {
        return this.posts.values();
    }

    public Collection<Candidate> findAllCandidates() {
        return this.candidates.values();
    }

    public void save(Post post) {
        if (post.getId() == 0) {
            post.setId(POST_ID.incrementAndGet());
        }
        this.posts.put(post.getId(), post);
    }

    public Post findPostById(int id) {
        return this.posts.get(id);
    }

    public void save(Candidate candidate) {
        if (candidate.getId() == 0) {
            candidate.setId(CAND_ID.incrementAndGet());
        }
        this.candidates.put(candidate.getId(), candidate);
    }

    public Candidate findCandidateById(int id) {
        return this.candidates.get(id);
    }

    public void deleteCandidateById(int id) {
        this.candidates.remove(id);
    }
}
