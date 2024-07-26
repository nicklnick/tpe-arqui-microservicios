import asyncio
import json

import pytest

from src.test.component.mocked_client import AsyncRabbitMQPublisher


@pytest.mark.asyncio
async def test_send_message():
    input_message = "Hello, RabbitMQ!"
    expected_response = "Received a mocked response"

    publisher = AsyncRabbitMQPublisher()
    await publisher.connect()

    await publisher.send_message(input_message)

    await publisher.wait_for_response()

    assert hasattr(publisher, 'response'), "No response received"
    assert publisher.response['response'] == expected_response, f"Expected '{expected_response}', but got '{publisher.response['response']}'"
    assert publisher.response['input'] == input_message, f"Expected '{input_message}', but got '{publisher.response['input']}'"

    await publisher.close()

if __name__ == "__main__":
    pytest.main()