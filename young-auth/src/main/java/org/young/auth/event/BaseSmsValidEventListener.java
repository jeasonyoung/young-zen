package org.young.auth.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

import javax.annotation.Nonnull;

/**
 * 短信验证码-事件监听处理器。
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/28 01:43
 */
@Slf4j
public abstract class BaseSmsValidEventListener implements ApplicationListener<SmsValidEvent> {

    /**
     * 监听事件处理。
     * @param event
     * 事件数据。
     */
    @Override
    public void onApplicationEvent(@Nonnull final SmsValidEvent event) {
        log.debug("onApplicationEvent(event: {})...", event);
        this.smsValidEventDataHandler(event.getData());
    }

    /**
     * 验证事件数据处理。
     * @param data
     * 验证数据。
     */
    protected abstract void smsValidEventDataHandler(final SmsValidEvent.ValidData data);
}
