from sqlalchemy import Column, Integer, VARCHAR, TIMESTAMP
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()
Base.metadata.schema = "system"


class GuestLandingRow(Base):
    __tablename__ = "customer_landing"
    id = Column(Integer, primary_key=True, autoincrement=True)
    create_time = Column(TIMESTAMP)
    ip = Column(VARCHAR(255))
    ip_location = Column(VARCHAR(255))
    device = Column(VARCHAR(255))
    contact = Column(VARCHAR(255))
    landing_page = Column(VARCHAR(255))
