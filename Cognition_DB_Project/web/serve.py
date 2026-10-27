"""Serve the mobile-friendly Cognition browser preview on the local network."""
from http.server import ThreadingHTTPServer, SimpleHTTPRequestHandler
from pathlib import Path
import os

ROOT = Path(__file__).resolve().parent
os.chdir(ROOT)
server = ThreadingHTTPServer(("0.0.0.0", 8000), SimpleHTTPRequestHandler)
print("Cognition browser preview is running.")
print("On this laptop: http://localhost:8000")
print("On a phone on the same Wi-Fi: http://<LAPTOP-LAN-IP>:8000")
print("This is preview data only; it does not connect to Oracle.")
try:
    server.serve_forever()
except KeyboardInterrupt:
    print("Stopping preview server...")
finally:
    server.server_close()
