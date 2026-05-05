package com.nexusjobs.portal.service;

import com.nexusjobs.portal.model.Message;
import com.nexusjobs.portal.model.User;
import com.nexusjobs.portal.repository.MessageRepository;
import com.nexusjobs.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public List<Message> getThread(String threadId) {
        return messageRepository.findByThreadIdOrderByTimestampAsc(threadId);
    }

    /** Returns thread summaries for a user: [{threadId, other, lastMessage}] */
    public List<Map<String, Object>> getThreadsForUser(Long userId) {
        List<String> threadIds = messageRepository.findDistinctThreadIdsByUserId(userId);
        return threadIds.stream().map(tid -> {
            List<Message> msgs = messageRepository.findByThreadIdOrderByTimestampAsc(tid);
            if (msgs.isEmpty()) return null;
            Message last = msgs.get(msgs.size() - 1);
            Long otherId = last.getFromUser().getId().equals(userId)
                    ? last.getToUser().getId() : last.getFromUser().getId();
            User other = userRepository.findById(otherId).orElse(null);
            Map<String, Object> m = new HashMap<>();
            m.put("threadId", tid);
            m.put("other", other);
            m.put("lastMessage", last);
            m.put("messages", msgs);
            return m;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Transactional
    public Message send(User from, User to, String text, String threadId) {
        String tid = (threadId != null && !threadId.isBlank())
                ? threadId
                : "t_" + from.getId() + "_" + to.getId() + "_" + System.currentTimeMillis();
        Message msg = Message.builder()
                .fromUser(from)
                .toUser(to)
                .threadId(tid)
                .text(text)
                .build();
        return messageRepository.save(msg);
    }
}
