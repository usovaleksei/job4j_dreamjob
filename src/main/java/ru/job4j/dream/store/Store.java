package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Store {

    private static final Store INST = new Store();

    private static final AtomicInteger POST_ID = new AtomicInteger(4);
    private static final AtomicInteger CAND_ID = new AtomicInteger(4);

    private Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private Store() {
        posts.put(1, new Post(1, "Junior Java Job", "Write code as junior", LocalDateTime.now().minusHours(1)));
        posts.put(2, new Post(2, "Middle Java Job", "Write code as middle", LocalDateTime.now().minusHours(3)));
        posts.put(3, new Post(3, "Senior Java Job", "Write code as senior", LocalDateTime.now().minusHours(5)));
        candidates.put(1, new Candidate(1, "Junior JAva"));
        candidates.put(2, new Candidate(2, "Middle JAva"));
        candidates.put(3, new Candidate(3, "Senior JAva"));
    }

    public static Store instOf() {
        return INST;
    }

    public Collection<Post> findAllPosts() {
        return posts.values();
    }

    public Collection<Candidate> findAllCandidates() {
        return candidates.values();
    }

    public void save(Post post) {
        post.setId(POST_ID.incrementAndGet());
        posts.put(post.getId(), post);
    }

    public void save(Candidate candidate) {
        candidate.setId(CAND_ID.incrementAndGet());
        candidates.put(candidate.getId(), candidate);
    }
}
