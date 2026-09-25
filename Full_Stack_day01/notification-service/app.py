import json
from http.server import BaseHTTPRequestHandler, HTTPServer


def queue_notification(notification):
    return {"status": "notification queued", "order_id": notification["order_id"]}


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/health":
            self.respond(200, {"service": "notification-service", "status": "healthy"})
            return
        self.respond(404, {"error": "not found"})

    def do_POST(self):
        if self.path != "/notify":
            self.respond(404, {"error": "not found"})
            return
        notification = json.loads(self.rfile.read(int(self.headers.get("Content-Length", 0))))
        self.respond(202, queue_notification(notification))

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