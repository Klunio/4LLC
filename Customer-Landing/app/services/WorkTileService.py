from datetime import datetime
from typing import List

import httpx
from loguru import logger
from pydantic import BaseModel

from app.models.schema import CustomerInfo
from app.services import BaseService


class WorkTileCreateTaskException(BaseException):
    pass


class Assignee(BaseModel):
    value: str
    type: int = 2


class Properties(BaseModel):
    day: str
    worktime: str
    dizhiip: str
    pcormobile: str
    shoujihao: str
    wechat: str
    landing_com: str
    landingly_: str = 'landing时间'


class Start(BaseModel):
    date: str
    with_time: int = 1


class Payload(BaseModel):
    type: str = "新版landing任务"
    title: str
    assignee: Assignee
    start: Start
    properties: Properties


class WorkTileBody(BaseModel):
    action: str = "create_task"
    payload: Payload


class WorkTileService(BaseService):
    week2ch = dict(zip([1, 2, 3, 4, 5, 6, 7], '一二三四五六日'))
    url2page = {
        "www.mesoor.com/": "官网主页",
        "www.mesoor.com/talent-portrait.html": "人才画像",
        "www.mesoor.com/resume-insight.html": "简历解析",
        "www.mesoor.com/knowledge-map.html": "知识图谱",
        "www.mesoor.com/zhaohu-for-hr.html": "HR简历管理",
        "www.mesoor.com/extension-headhunter.html": "猎头简历管理",
        "www.mesoor.com/intelligent-interview.html": "智能面试",
        "www.mesoor.com/invitation-assistant.html": "智能机器人",
        "www.mesoor.com/qa-bot.html": "HRSSC",
        "www.mesoor.com/ai-interview.html": "胜任力分析",
        "www.mesoor.com/acceleration-engine.html": "人岗匹配",
        "www.mesoor.com/mesoor.html": "关于麦穗",
        "www.mesoor.com/news.html": "行业新动向",
        "kassandra.mesoor.com/dashboard#/start/login": "HRSSC",
        "system.mesoor.com/dashboard/#/jobs": "招聘机器人",
    }

    def __init__(self,
                 income_url: str,
                 assignee: str, **kwargs):
        super().__init__(**kwargs)
        self.assignee: List[str] = assignee.split(';')
        self.income_url: str = income_url
        logger.info(f'[Worktile] Init: income url [{self.income_url}], assignee[{self.assignee}]')

    @staticmethod
    def _is_work_time(now: datetime):
        day_of_week, hour_of_day = now.isoweekday(), now.hour
        return "是" if (1 <= day_of_week <= 5 and 9 <= hour_of_day <= 18) else "否"

    async def _send_impl(self, info: CustomerInfo):
        tele, wechat = self._get_tel_or_wechat(info.contact)
        worktile_body = WorkTileBody(
            payload=Payload(
                title=f"{info.create_time.year}-{info.create_time.month}-{info.count + 1}",
                assignee=Assignee(
                    value=self.assignee[info.count % len(self.assignee)] if len(self.assignee) > 1 else self.assignee[0] if len(self.assignee) == 1 else ''
                ),
                start=Start(date=round(info.create_time.timestamp())),
                properties=Properties(
                    day=self.week2ch[info.create_time.isoweekday()],
                    worktime=self._is_work_time(info.create_time),
                    dizhiip=info.ip_location,
                    pcormobile=self._get_device(info.other.get('ua', '')),
                    shoujihao=tele,
                    wechat=wechat,
                    landing_com=self.url2page.get(info.url.lstrip('http://').lstrip('https://'), '官网主页'),
                    leads=info.remarks
                )
            )
        )
        async with httpx.AsyncClient() as client:
            response = (await client.post(self.income_url,
                                          headers={"Content-Type": "application/json"},
                                          json=worktile_body.dict())
                        ).json()

            code = response.get('code', -1)
            if code != 200:
                raise Exception(f"[Worktile] Failed to add task, error code {code} => {response}")

        logger.info('[Worktile] Adding task succeed!')
