import asyncio
from datetime import datetime

from loguru import logger
from sqlalchemy import func
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.future import select
from sqlalchemy.orm import sessionmaker

from app.models.Tables import GuestLandingRow, Base
from app.models.schema import CustomerInfo
from app.services import BaseService


class PostgreSqlService(BaseService):

    def __init__(self,
                 username: str,
                 password: str,
                 host: str,
                 database: str,
                 **kwargs) -> None:
        super().__init__(**kwargs)

        self.engine = create_async_engine(f"postgresql+asyncpg://{username}:{password}@{host}/{database}")
        self.async_session = sessionmaker(
            self.engine, expire_on_commit=False, class_=AsyncSession
        )
        logger.info(f'[Postgres] Init postgresql: {host}/{database}')

    async def begin(self):
        async with self.engine.begin() as conn:
            await conn.run_sync(Base.metadata.create_all)

    async def get_count_this_month(self):
        async with self.async_session() as session:
            year, month = datetime.now().year, datetime.now().month
            return (await session.execute(
                select(func.count(GuestLandingRow.id))
                    .where(GuestLandingRow.create_time >= datetime(year, month, day=1))
            )).one()[0]

    async def _send_impl(self, info: CustomerInfo):
        async with self.async_session() as session:
            session.add(GuestLandingRow(create_time=info.create_time,
                                        ip=info.ip,
                                        ip_location=info.ip_location,
                                        device=self._get_device(info.other.get('ua', '')),
                                        contact=info.contact,
                                        landing_page=info.url)
                        )
            await session.commit()

        logger.info('[Postgres] Inserting succeed!')


if __name__ == '__main__':
    import os
    pg = PostgreSqlService(**{
        "username": os.getenv("DEFAULT_PG_USER", "mesoor"),
        "password": os.getenv("DEFAULT_PG_PASSWORD", "R3v6IQP2pMvRHKiCYDsnIlBe0EkrjCRp!DXbsAi)7P6!lH#)q5"),
        "host": os.getenv("DEFAULT_PG_HOST",
                          "mesoor-product.cupzbhodsxus.rds.cn-northwest-1.amazonaws.com.cn:5432"),
        'database': os.getenv("DEFAULT_PG_DB", "mesoor")

    })

    #
    import pandas as pd

    df = pd.read_excel('~/Downloads/Worktile.Project.官网landing20210421030849.xlsx')
    L = len(df)


    async def run():
        async with pg.async_session() as session:
            for i, row in df.iterrows():
                try:
                    create_time = datetime.strptime(row['开始时间'], '%Y-%m-%d %H:%M')
                except:
                    create_time = datetime.strptime(row['开始时间'], '%Y-%m-%d')
                print(create_time)

                session.add(GuestLandingRow(id=L - i,
                                            create_time=create_time,
                                            ip='None',
                                            ip_location=str(row['地址（IP所在地）']),
                                            device=row['PC/移动'],
                                            contact=str(row['手机号']),
                                            landing_page=str(row['landing页面'])
                                            ))
            await session.commit()


    asyncio.run(run())
