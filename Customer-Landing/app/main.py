import glob
import os
from datetime import datetime
from typing import Optional

import aiofiles
from fastapi import FastAPI, Request, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse, PlainTextResponse
from loguru import logger

from app.CustomerManagement import CustomerManagement, CustomerInfoSendingException
from app.config import DEFAULT_ENDPOINT_SERVICES, init_logging
from app.models.schema import CustomerInfo
from app.schedule import Schedule

app: FastAPI = FastAPI(debug=False)
CM: CustomerManagement = CustomerManagement()

origins = [
    "*.nadileaf.com",
    '*'
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["POST", "GET", "OPTIONS"],
    allow_headers=["*"],
)


# TODO:
#  API: (de)register endpoint service

@logger.catch
@app.on_event("startup")
async def startup():
    init_logging()

    logger.info("Init GuestLanding")
    for service_name, config in DEFAULT_ENDPOINT_SERVICES:
        CM.register_service(service_name, config)

    logger.info("[Schedule] add count load job.")
    # Schedule.add_job(func=CM.empty_count,
    #                  trigger='cron',
    #                  year='*', month='*', day='1')
    Schedule.add_job(func=CM.load_count,
                     trigger='interval',
                     seconds=900,
                     next_run_time=datetime.now())


@app.post('/api/visitor/registration')
async def customer_landing(request: Request, guest_info: CustomerInfo):
    try:
        guest_info.ip = request.client.host
        logger.info('Receive guest landing request - %s ' % guest_info)
        await CM.deliver_landing_info(guest_info)
        return JSONResponse(status_code=200, content={"message": "ok"})
    except CustomerInfoSendingException as e:
        logger.error("Some services may fail.")
        return JSONResponse(status_code=200, content={"message": str(e)})
    except BaseException as e:
        return HTTPException(400, detail={"message": str(e)})
    finally:
        await CM.count_add()


@app.get('/list_logs')
async def list_logs():
    return JSONResponse(status_code=200, content=glob.glob('./log/*'))


@app.get('/logs/{name}', response_class=PlainTextResponse)
async def logs(name: Optional[str]):
    log_path = f'./log/{name}' if os.path.exists(f'./log/{name}') else sorted(glob.glob('./log/*'))[-1]
    async with aiofiles.open(log_path) as f:
        return await f.read()


@app.on_event('shutdown')
async def shutdown():
    logger.info("shut down")
