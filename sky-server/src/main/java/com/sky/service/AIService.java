
package com.sky.service;

public interface AIService {

    /**
     * AI 智能客服对话（带会话记忆）
     * @param sessionId 会话 ID（用户唯一标识）
     * @param message 用户消息
     * @return AI 回复
     */
    String chat(String sessionId, String message);

    /**
     * 清除会话历史
     * @param sessionId 会话 ID
     */
    void clearMemory(String sessionId);

    /**
     * 获取会话历史
     * @param sessionId 会话 ID
     * @return 会话历史消息列表
     */
    java.util.List<String> getChatHistory(String sessionId);
}