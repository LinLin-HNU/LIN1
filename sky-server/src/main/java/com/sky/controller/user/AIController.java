
package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.AIService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/ai")
@Api(tags = "AI智能客服接口")
@Slf4j
public class AIController {

    @Autowired
    private AIService aiService;



    /**
     * AI 智能客服对话（带会话记忆）
     * @param sessionId 会话 ID（前端生成或用户 ID）
     * @param message 用户消息
     */
    @PostMapping("/chat")
    @ApiOperation("AI智能客服对话")
    public Result<String> chat(@RequestParam(defaultValue = "test_session_001") String sessionId,
                               @RequestBody String message) {
        log.info("AI客服收到消息 - 会话ID: {}, 消息: {}", sessionId, message);
        String response = aiService.chat(sessionId, message);
        return Result.success(response);
    }

    /**
     * 获取会话历史
     */
    @GetMapping("/history/{sessionId}")
    @ApiOperation("获取会话历史")
    public Result<List<String>> getHistory(@PathVariable String sessionId) {
        List<String> history = aiService.getChatHistory(sessionId);
        return Result.success(history);
    }

    /**
     * 清除会话记忆
     */
    @DeleteMapping("/memory/{sessionId}")
    @ApiOperation("清除会话记忆")
    public Result<String> clearMemory(@PathVariable String sessionId) {
        aiService.clearMemory(sessionId);
        return Result.success("会话记忆已清除");
    }
}