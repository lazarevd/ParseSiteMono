package ru.laz.sender;

import com.fasterxml.jackson.core.base.ParserBase;
import ru.laz.common.models.NewsBlockDTO;
import ru.laz.common.models.NewsBlockSendStatusDTO;

public interface SenderListener {
    public void convertAndSend(NewsBlockDTO newsBlockDTO);
}
