import importlib.util
import pathlib
import unittest


ROOT = pathlib.Path(__file__).parents[1]


def load_service(name, path):
    spec = importlib.util.spec_from_file_location(name, ROOT / path)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


order_api = load_service("order_api", "order-api/app.py")
order_processor = load_service("order_processor", "order-processor/app.py")
notification_service = load_service("notification_service", "notification-service/app.py")


class OrderProcessingTests(unittest.TestCase):
    def test_order_api_creates_order(self):
        order = order_api.create_order({"item": "keyboard", "quantity": 2})
        self.assertEqual(order["item"], "keyboard")
        self.assertEqual(order["quantity"], 2)
        self.assertTrue(order["order_id"])

    def test_processor_creates_notification_payload(self):
        result = order_processor.process_order({"order_id": "order-123"})
        self.assertEqual(result, {
            "order_id": "order-123",
            "message": "Order processed successfully",
        })

    def test_notification_queues_order(self):
        result = notification_service.queue_notification({"order_id": "order-123"})
        self.assertEqual(result["status"], "notification queued")
        self.assertEqual(result["order_id"], "order-123")


if __name__ == "__main__":
    unittest.main()