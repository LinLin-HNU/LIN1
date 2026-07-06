
package com.sky.service.impl;

import com.sky.service.AIService;
import com.sky.service.AITools;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.dashscope.QwenChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import dev.langchain4j.data.message.ChatMessage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AIServiceImpl implements AIService {

    @Value("${langchain4j.dashscope.api-key}")
    private String apiKey;

    @Value("${langchain4j.dashscope.model-name:qwen-plus}")
    private String modelName;

    @Value("${langchain4j.dashscope.temperature:0.7}")
    private Float temperature;

    @Autowired
    private AITools aiTools;

    private QwenChatModel chatModel;

    // 会话记忆存储（Key: sessionId, Value: ChatMemory）
    private final Map<String, MessageWindowChatMemory> chatMemories = new ConcurrentHashMap<>();

    // 最大记忆消息数量（保留最近 20 条消息）
    private static final int MAX_MESSAGES = 30;

    private QwenChatModel getChatModel() {
        if (chatModel == null) {
            chatModel = QwenChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(modelName)
                    .build();
        }
        return chatModel;
    }

    /**
     * 获取或创建会话记忆
     */
    private MessageWindowChatMemory getOrCreateMemory(String sessionId) {
        return chatMemories.computeIfAbsent(sessionId,
                id -> MessageWindowChatMemory.withMaxMessages(MAX_MESSAGES));
    }

    /**
     * AI 对话
     * @param sessionId 会话 ID（用户唯一标识）
     * @param message 用户消息
     * @return
     */
    @Override
    public String chat(String sessionId, String message) {
        log.info("========== AI 对话开始 ==========");
        log.info("【会话 ID】{}", sessionId);
        log.info("【输入前】用户消息: {}", message);

        // 获取会话记忆
        MessageWindowChatMemory memory = getOrCreateMemory(sessionId);

        // 构建 AI 服务（带记忆）
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(getChatModel())
                .chatMemory(memory)
                .tools(aiTools)
                .build();   //这里返回的是动态代理对象

        // 调用 AI
        String response = assistant.chat(message);  //这行代码触发了整个AI对话流程

        // 记录 Token 使用
        log.info("【输出后】AI 回复: {}", response);
        log.info("【记忆条数】当前会话已保存 {} 条消息", memory.messages().size());
        log.info("========== AI 对话结束 ==========");

        return response;
    }

    /**
     * 清除会话记忆
     * @param sessionId 会话 ID
     */
    @Override
    public void clearMemory(String sessionId) {
        log.info("清除会话记忆: {}", sessionId);
        chatMemories.remove(sessionId);
    }

    /**
     * 得到会话历史
     * @param sessionId 会话 ID
     * @return
     */
    @Override
    public List<String> getChatHistory(String sessionId) {
        MessageWindowChatMemory memory = chatMemories.get(sessionId);
        if (memory == null) {
            return List.of();
        }
        return memory.messages().stream()
                .map(ChatMessage::text)
                .collect(Collectors.toList());
    }

    /**
     * AI 助手接口定义
     * 这是langchain4j的核心，AI服务接口（通过动态代理实现）
     */
    interface Assistant {
        @SystemMessage("你是苍穹外卖的智能客服助手。你可以帮助用户：\n" +
                "1. 查询菜品信息\n" +
                "2. 查询用户个人信息\n" +
                "3. 修改用户手机号和地址\n" +
                "\n" +
                "请用友好、专业的语气回答。如果用户询问超出业务范围的问题，请礼貌地引导回外卖相关话题。")
        String chat(String userMessage);
    }



}