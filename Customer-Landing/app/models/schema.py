from datetime import datetime
from typing import Optional

from pydantic import BaseModel, Field


class CustomerInfo(BaseModel):
    contact: str
    intention: str
    url: str
    other: Optional[dict] = {}
    remarks: Optional[str] = ''

    create_time: datetime = Field(default_factory=datetime.now)
    ip: str = "None"
    ip_location: str = "None"
    count: int = 0
