from apscheduler.events import EVENT_JOB_ERROR
from apscheduler.executors.asyncio import AsyncIOExecutor
from apscheduler.schedulers.asyncio import AsyncIOScheduler
from loguru import logger

try:
    logger.info("Init Schedule")
    Schedule = AsyncIOScheduler(
        executors={
            'default': AsyncIOExecutor()
        },
        timezone='Asia/Shanghai',
    )
    Schedule.add_listener(lambda e: logger.exception(e.exception), EVENT_JOB_ERROR)
    Schedule.start()
except BaseException as e:
    logger.exception(e)
