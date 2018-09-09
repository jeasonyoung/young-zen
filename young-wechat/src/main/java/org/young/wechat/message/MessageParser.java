package org.young.wechat.message;

import com.google.common.base.Strings;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import lombok.extern.slf4j.Slf4j;
import org.young.wechat.message.callback.BaseCallbackMessage;
import org.young.wechat.message.receives.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息解析器。
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/8/8 17:37
 */
@Slf4j
public class MessageParser {
    private static final Map<MessageType, MessageParseHandler> NORMAL_PARSE_MAPS = new HashMap<>();
    private static final Map<MessageEventType, MessageParseHandler> EVENT_PARSE_MAPS = new HashMap<>();

    static {
        //普通消息解析
        NORMAL_PARSE_MAPS.put(MessageType.TEXT, xml-> createXStreamParser(TextMessage.class, xml));
        NORMAL_PARSE_MAPS.put(MessageType.IMAGE, xml -> createXStreamParser(ImageMessage.class, xml));
        NORMAL_PARSE_MAPS.put(MessageType.VOICE, xml -> createXStreamParser(VoiceMessage.class, xml));
        NORMAL_PARSE_MAPS.put(MessageType.VIDEO, xml -> createXStreamParser(VideoMessage.class, xml));
        NORMAL_PARSE_MAPS.put(MessageType.SHORTVIDEO, xml -> createXStreamParser(ShortVideoMessage.class, xml));
        NORMAL_PARSE_MAPS.put(MessageType.LOCATION, xml -> createXStreamParser(LocationMessage.class, xml));
        NORMAL_PARSE_MAPS.put(MessageType.LINK, xml -> createXStreamParser(LinkMessage.class, xml));
        //事件消息解析
        EVENT_PARSE_MAPS.put(MessageEventType.SUBSCRIBE, xml -> createXStreamParser(SubscribeEventMessage.class, xml));
        EVENT_PARSE_MAPS.put(MessageEventType.SCAN, xml -> createXStreamParser(ScanEventMessage.class, xml));
        EVENT_PARSE_MAPS.put(MessageEventType.LOCATION, xml -> createXStreamParser(LocationEventMessage.class, xml));
        EVENT_PARSE_MAPS.put(MessageEventType.CLICK, xml -> createXStreamParser(ClickEventMessage.class, xml));
        EVENT_PARSE_MAPS.put(MessageEventType.VIEW, xml -> createXStreamParser(ViewEventMessage.class, xml));
    }

    private static <T extends BaseMessage> T createXStreamParser(@Nonnull final Class<T> clazz, @Nonnull final String xml) {
        try {
            final XStream xStream = new XStream(new DomDriver());
            xStream.autodetectAnnotations(true);
            xStream.processAnnotations(clazz);
            xStream.alias(BaseMessage.ROOT, clazz);
            xStream.ignoreUnknownElements();
            return clazz.cast(xStream.fromXML(xml));
        }catch (Throwable ex){
            log.warn("createXStreamParser(clazz: "+ clazz +", xml:"+ xml +")-exp:" + ex.getMessage(), ex);
        }
        return null;
    }

    private static <T extends BaseCallbackMessage> String toXml(@Nullable T data){
        if(data != null){
            try {
                final Class<?> clazz = data.getClass();
                final XStream xStream = new XStream(new DomDriver());
                xStream.autodetectAnnotations(true);
                xStream.processAnnotations(clazz);
                xStream.alias(BaseCallbackMessage.ROOT, clazz);
                return xStream.toXML(data);
            }catch (Throwable ex){
                log.warn("toXml(data: "+ data +")-exp:" + ex.getMessage(), ex);
            }
        }
        return null;
    }


    /**
     * 解析报文处理。
     * @param xml
     * xml报文。
     * @param listener
     * 报文处理。
     * @return 反馈xml数据。
     */
    public static String parse(@Nonnull final String xml, @Nonnull final MessageHandlerListener listener){
        log.debug("parse(xml: \n {} \n, listener: {})...", xml, listener);
        try{
            //解析为基本消息
            final BaseMessage baseMessage = createXStreamParser(BaseMessage.class, xml);
            if(baseMessage == null || Strings.isNullOrEmpty(baseMessage.getMsgType())){
                log.warn("parse-消息类失败=>{}", xml);
                return null;
            }
            //检查消息类型
            final MessageType type = MessageType.parse(baseMessage.getMsgType());
            if(type == null){
                log.warn("parse-消息类型不存在!=>{}", baseMessage);
                return null;
            }
            //消息处理器
            MessageParseHandler parseHandler;
            //是否事件消息
            boolean hasEvent = false;
            MessageEventType eventType = null;
            //事件消息处理
            if(type == MessageType.EVENT){
                //解析为事件消息
                final BaseEventMessage baseEventMessage = createXStreamParser(BaseEventMessage.class, xml);
                if(baseEventMessage == null || Strings.isNullOrEmpty(baseEventMessage.getEvent())){
                    log.warn("parse-解析为事件消息失败!=>{}", baseEventMessage);
                    return null;
                }
                //事件消息类型解析
                eventType = MessageEventType.parse(baseEventMessage.getEvent());
                if(eventType == null){
                    log.warn("parse-解析为事件消息类型失败!=>{}", baseEventMessage);
                    return null;
                }
                //事件消息解析器
                parseHandler = EVENT_PARSE_MAPS.get(eventType);
                hasEvent = true;
            }else{
                //普通消息解析器
                parseHandler = NORMAL_PARSE_MAPS.get(type);
            }
            //检查消息解析器
            if(parseHandler != null){
                //解析消息处理
                final BaseCallbackMessage callback = hasEvent ? listener.eventMessage(eventType, (BaseEventMessage)parseHandler.parse(xml)) : listener.normalMessage(type, (BaseNormalMessage)parseHandler.parse(xml));
                if(callback != null){
                    log.info("parse-反馈消息: {}", callback);
                    return toXml(callback);
                }
            }
        }catch (Throwable ex){
            log.warn("MessageParser-parse-exp:" + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 消息处理监听器
     */
    public interface MessageHandlerListener<T extends BaseCallbackMessage> {

        /**
         * 普通消息处理。
         * @param type
         * 消息类型。
         * @param data
         * 消息数据。
         * @return 反馈数据。
         */
       T normalMessage(@Nonnull final MessageType type, @Nonnull final BaseNormalMessage data);

        /**
         * 事件消息处理。
         * @param eventType
         * 事件类型。
         * @param data
         * 消息数据。
         * @return 反馈数据。
         */
        T eventMessage(@Nonnull final MessageEventType eventType,@Nonnull final BaseEventMessage data);
    }

    /**
     * 消息解析处理器
     */
    private interface MessageParseHandler<T extends BaseMessage> {
        /**
         * 解析xml报文。
         *
         * @param xml xml报文。
         * @return 消息数据
         */
        T parse(@Nonnull final String xml);
    }
}
