import json
import os
import urllib.request
from http.server import BaseHTTPRequestHandler, HTTPServer
from uuid import uuid4


processor_url = os.getenv("ORDER_PROCESSOR_URL", "http://order-processor:8000")
PROJECT_NAME = "Shubham's Order Platform"
PROJECT_VERSION = "1.0.0"


def create_order(payload):
    return {
        "order_id": str(uuid4()),
        "item": payload["item"],
        "quantity": payload["quantity"],
    }


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/":
            self.respond(200, {
                "service": "order-api",
                "project": PROJECT_NAME,
                "version": PROJECT_VERSION,
                "endpoints": {
                    "health": "GET /health",
                    "create_order": "POST /orders",
                },
            })
            return
        if self.path == "/health":
            self.respond(200, {"service": "order-api", "status": "healthy"})
            return
        self.respond(404, {"error": "not found"})

    def do_POST(self):
        if self.path != "/orders":
            self.respond(404, {"error": "not found"})
            return
        try:
            payload = json.loads(self.rfile.read(int(self.headers.get("Content-Length", 0))))
            order = create_order(payload)
            request = urllib.request.Request(
                f"{processor_url}/process",
                data=json.dumps(order).encode(),
                headers={"Content-Type": "application/json"},
                method="POST",
            )
            with urllib.request.urlopen(request, timeout=3) as response:
                processing = json.loads(response.read())
            self.respond(201, {"order": order, "processing": processing})
        except (KeyError, ValueError, urllib.error.URLError):
            self.respond(400, {"error": "provide item and quantity; processor must be reachable"})

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