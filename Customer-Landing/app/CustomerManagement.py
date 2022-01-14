import asyncio
from typing import List

import httpx
from loguru import logger

import app.services as Service
from app.models.schema import CustomerInfo


class CustomerInfoSendingException(BaseException):
    pass


class CustomerManagement:

    def __init__(self, ):
        self._server: List[Service.BaseService] = []
        self.count_this_month: int = 0
        self.count_lock = asyncio.Lock()

    def register_service(self, service_name, config) -> None:
        logger.info(f"Register service {service_name}")
        self._server.append(
            getattr(Service, service_name)(**config)
        )

    async def load_count(self) -> None:
        for service in self._server:
            if isinstance(service, Service.PostgreSqlService):
                self.count_this_month = await service.get_count_this_month()
                logger.info("Load count this month - %d" % self.count_this_month)
                return
        raise Exception("Failed to load count since there is no PostgreSql Service registered!")

    async def count_add(self):
        async with self.count_lock:
            self.count_this_month += 1
            logger.info("Update count of this month %s" % self.count_this_month)

    async def empty_count(self) -> None:
        async with self.count_lock:
            self.count_this_month = 0
            logger.info("Empty count")

    @staticmethod
    async def _query_ip_location(remote_host: str) -> str:
        async with httpx.AsyncClient() as client:
            try:
                response = (await client.get(f"http://ip-api.com/json/{remote_host}?lang=zh-CN")).json()
            except httpx.TimeoutException:
                logger.info('[CustomerManagement] Looking up ip location timeout, retry...')
                await asyncio.sleep(1.)
                response = (await client.get(f"http://ip-api.com/json/{remote_host}?lang=zh-CN")).json()

        if response['status'] == 'success':
            location = "{}-{}".format(response.get('regionName', ''), response.get('city', ''))
            logger.info("[CustomerManagement] Succeed to query for ip location %s -> %s" % (remote_host, location))
            return location
        else:
            logger.info("[CustomerManagement] Failed to query for ip location %s, message %s" % (
                remote_host, response.get('message', 'Unknown')))
            return "NOT-FOUND"

    async def deliver_landing_info(self, guest_info: CustomerInfo) -> None:
        # 补充 info
        guest_info.ip_location = await self._query_ip_location(guest_info.ip)
        guest_info.count = self.count_this_month

        msg = []
        for result in await asyncio.gather(*map(lambda s: s.send(info=guest_info), self._server),
                                           return_exceptions=True):
            if isinstance(result, BaseException):
                msg.append(repr(result))
        if msg:
            raise CustomerInfoSendingException('\n'.join(msg))
