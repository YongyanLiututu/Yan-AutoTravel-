#!/usr/bin/env python3
"""
Image thumbnail worker: fetch image_url → write thumb (no hotlink bypass, best effort).
Outputs CSV with columns: origin_url, thumb_path, status
"""
import concurrent.futures
import csv
import os
import sys
from pathlib import Path
from io import BytesIO

import requests
from PIL import Image


def fetch_and_thumb(url: str, out_dir: Path, size=(480, 320)):
    try:
        r = requests.get(url, timeout=8, headers={"User-Agent": "YuManusImageProxy/0.1"})
        r.raise_for_status()
        img = Image.open(BytesIO(r.content)).convert("RGB")
        img.thumbnail(size)
        name = str(abs(hash(url))) + ".webp"
        path = out_dir / name
        img.save(path, format="WEBP", quality=86)
        return url, str(path), "ok"
    except Exception as e:
        return url, "", f"fail:{e.__class__.__name__}"


def main():
    if len(sys.argv) < 2:
        print("Usage: image_jobs.py poi_normalized.csv", file=sys.stderr)
        sys.exit(1)
    csv_path = Path(sys.argv[1])
    out_dir = Path(__file__).parent / "thumbs"
    out_dir.mkdir(parents=True, exist_ok=True)
    out_meta = Path(__file__).parent / "thumbs_index.csv"

    urls = []
    with open(csv_path, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            u = row.get("image_url")
            if u:
                urls.append(u)

    with concurrent.futures.ThreadPoolExecutor(max_workers=16) as ex, open(out_meta, "w", newline="", encoding="utf-8") as outf:
        w = csv.writer(outf)
        w.writerow(["origin_url", "thumb_path", "status"])
        for url, path, status in ex.map(lambda u: fetch_and_thumb(u, out_dir), urls):
            w.writerow([url, path, status])
    print(f"Processed {len(urls)} images → {out_dir}")


if __name__ == "__main__":
    main()


