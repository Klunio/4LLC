import abc
import re
from typing import Tuple

from loguru import logger

from app.models.schema import CustomerInfo

teleRegx = re.compile("^((13[0-9])|(14[5,79])|(15[^4])|(18[0-9])|(17[0,135678]))[0-9]{8}$")


class BaseService(metaclass=abc.ABCMeta):

    @abc.abstractmethod
    def __init__(self, **kwargs):
        pass

    @abc.abstractmethod
    async def _send_impl(self, info: CustomerInfo):
        raise NotImplemented

    async def send(self, info: CustomerInfo):
        logger.info(f"[Service] Send from {self.__class__}")
        await self._send_impl(info=info)

    @staticmethod
    def _get_device(ua: str) -> str:
        return "移动" if ua.find("Mobile") != -1 else "PC"

    @staticmethod
    def _get_tel_or_wechat(text: str) -> Tuple[str, str]:
        return (text, "") if teleRegx.match(text[-11:]) else ("", text)
