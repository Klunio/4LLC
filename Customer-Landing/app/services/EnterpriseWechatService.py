import json

import httpx
from cachetools import TTLCache
from loguru import logger

from app.models.schema import CustomerInfo
from app.services import BaseService


class AccessTokenException(BaseException):
    pass


class EnterpriseSendException(BaseException):
    pass


class EnterpriseWechatService(BaseService):
    def __init__(self,
                 corp_id: str,
                 corp_secret: str,
                 agent_id: str,
                 **kwargs) -> None:
        super().__init__(**kwargs)
        self.corp_id = corp_id
        self.corp_secret = corp_secret
        self.agent_id = agent_id
        self.cache = TTLCache(maxsize=10, ttl=3600)
        logger.info(f'[企业微信] Init: corp id [{self.corp_id}] agent id [{self.agent_id}]')

    async def get_access_token(self) -> str:
        if 'access_token' not in self.cache:
            self.cache['access_token'] = await self._get_access_token_impl()
        return self.cache['access_token']

    async def _get_access_token_impl(self) -> str:
        async with httpx.AsyncClient() as client:
            response = (await client.post("https://qyapi.weixin.qq.com/cgi-bin/gettoken", params={
                "corpid": self.corp_id,
                "corpsecret": self.corp_secret
            })).json()
            if response['errcode'] != 0:
                raise AccessTokenException(
                    f"[企业微信] failed to get access token, errcode: {response['errcode']}, msg: {response['errmsg']}")
            return response['access_token']

    def _make_body(self, message):
        return {
            "toparty": "13",
            "msgtype": "text",
            "agentid": self.agent_id,
            "text": {
                "content": message
            },
            "safe": 0,
            "enable_id_trans": 0,
            "enable_duplicate_check": 0,
            "duplicate_check_interval": 1800,
        }

    async def _send_impl(self, info: CustomerInfo) -> None:
        message = f"""
        本月第{info.count+1}条
        IP: {info.ip}: {info.ip_location}
        URL: {info.url}
        联系方式: {info.contact}
        其他: {json.dumps(info.other, indent=4, ensure_ascii=True)}
        """
        body = self._make_body(message)
        async with httpx.AsyncClient() as client:
            access_token = await self.get_access_token()
            response = (await client.post("https://qyapi.weixin.qq.com/cgi-bin/message/send",
                                          params={"access_token": access_token},
                                          json=body)
                        ).json()

            if response['errcode'] != 0:
                raise EnterpriseSendException(
                    f"[企业微信] Failed to send message, errcode: {response['err']}, msg: {response['errmsg']}")

        logger.info('[企业微信] Sending succeed!')
