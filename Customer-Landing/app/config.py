import logging
import os
import sys
from pprint import pformat
from typing import List, Tuple, Dict

from loguru import logger
from loguru._defaults import LOGURU_FORMAT


class InterceptHandler(logging.Handler):
    """
    Default handler from examples in loguru documentaion.
    See https://loguru.readthedocs.io/en/stable/overview.html#entirely-compatible-with-standard-logging
    """

    def emit(self, record: logging.LogRecord):
        # Get corresponding Loguru level if it exists
        try:
            level = logger.level(record.levelname).name
        except ValueError:
            level = record.levelno

        # Find caller from where originated the logged message
        frame, depth = logging.currentframe(), 2
        while frame.f_code.co_filename == logging.__file__:
            frame = frame.f_back
            depth += 1

        logger.opt(depth=depth, exception=record.exc_info).log(
            level, record.getMessage()
        )


def format_record(record: dict) -> str:
    format_string = LOGURU_FORMAT
    if record["extra"].get("payload") is not None:
        record["extra"]["payload"] = pformat(
            record["extra"]["payload"], indent=4, compact=True, width=88
        )
        format_string += "\n<level>{extra[payload]}</level>"

    format_string += "{exception}\n"
    return format_string


def init_logging():
    loggers = (
        logging.getLogger(name)
        for name in logging.root.manager.loggerDict
        if name.startswith("uvicorn.")
    )
    intercept_handler = InterceptHandler()

    for uvicorn_logger in loggers:
        uvicorn_logger.handlers = [intercept_handler]

    logger.configure(
        handlers=[{"sink": sys.stdout, "level": logging.INFO, "format": format_record}]
    )
    logger.add('./log/runtime-{time}.log', rotation='1 week', retention='30 days')


DEFAULT_ENDPOINT_SERVICES: List[Tuple[str, Dict[str, str]]] = [
    ("WorkTileService", {
        'income_url': os.getenv("DEFAULT_WT_URL",
                                "https://hook.worktile.com/project/incoming/bd5e890220c443c2ac8911d5f2a11502"),
        'assignee': os.getenv("DEFAULT_WT_ASSIGNEE", "yurizhang@mesoor.com;ericwang@mesoor.com")
    }),

    ("PostgreSqlService", {
        "username": os.getenv("DEFAULT_PG_USER", "mesoor"),
        "password": os.getenv("DEFAULT_PG_PASSWORD", "R3v6IQP2pMvRHKiCYDsnIlBe0EkrjCRp!DXbsAi)7P6!lH#)q5"),
        "host": os.getenv("DEFAULT_PG_HOST",
                          "mesoor-develop.cupzbhodsxus.rds.cn-northwest-1.amazonaws.com.cn:5432"),
        'database': os.getenv("DEFAULT_PG_DB", "postgres")

    }),

    ('EnterpriseWechatService', {
        "corp_id": os.getenv("DEFAULT_WECHAT_CORP_ID", "wwc43b5754a0844416"),
        "corp_secret": os.getenv("DEFAULT_WECHAT_CORP_SECRET", "eFKRom5gjiXLCF3y7xN0234Xmx5N0wHTiuZ3clm3IoA"),
        "agent_id": os.getenv("DEFAULT_WECHAT_AGENT_ID", "1000002")
    })
]

