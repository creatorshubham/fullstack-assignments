import json
import os
import urllib.request
from http.server import BaseHTTPRequestHandler, HTTPServer


notification_url = os.getenv("NOTIFICATION_URL", "http://notification-service:8000")


def process_order(order):
    return {
        "order_id": order["order_id"],
        "message": "Order processed successfully",
    }


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/health":
            self.respond(200, {"service": "order-processor", "status": "healthy"})
            return
        self.respond(404, {"error": "not found"})

    def do_POST(self):
        if self.path != "/process":
            self.respond(404, {"error": "not found"})
            return
        order = json.loads(self.rfile.read(int(self.headers.get("Content-Length", 0))))
        notification = process_order(order)
        request = urllib.request.Request(
            f"{notification_url}/notify",
            data=json.dumps(notification).encode(),
            headers={"Content-Type": "application/json"},
            method="POST",
        )
        with urllib.request.urlopen(request, timeout=3) as response:
            notification_result = json.loads(response.read())
        self.respond(200, {"order_id": order["order_id"], "status": "processed", "notification": notification_result})

    def respond(self, status, payload):
        body = json.dumps(payload).encode()
        self.send_response(status)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def log_message(self, format, *args):
        return


if __name__ == "__main__":
    HTTPServer(("0.0.0.0", 8000), Handler).serve_forever()