#!/usr/bin/env python3
"""
Ingest HyperAI city CSVs into a normalized schema.

Input folder: CITYDATA_DIR env or default to Windows path.
Output: vector-ingest/output/poi_normalized.csv

Standard columns:
 city, province, poi_name, url, address, intro, open_hours, image_url, rating, duration, season, ticket, tips
"""
import os
import sys
import glob
import hashlib
from pathlib import Path

import pandas as pd


STANDARD_COLS = [
    "city", "province", "poi_name", "url", "address", "intro",
    "open_hours", "image_url", "rating", "duration", "season", "ticket", "tips"
]


def normalize_columns(df: pd.DataFrame) -> pd.DataFrame:
    col_map = {c.lower().strip(): c for c in df.columns}
    rename = {}
    # heuristic mapping
    for c in df.columns:
        lc = c.lower().strip()
        if lc in {"name", "scenic_name", "poi", "title"}:
            rename[c] = "poi_name"
        elif lc in {"img", "image", "image_url", "pic"}:
            rename[c] = "image_url"
        elif lc in {"desc", "intro", "introduction", "summary"}:
            rename[c] = "intro"
        elif lc in {"open", "open_hours", "hours", "time"}:
            rename[c] = "open_hours"
        elif lc in {"city"}:
            rename[c] = "city"
        elif lc in {"province"}:
            rename[c] = "province"
        elif lc in {"address", "addr"}:
            rename[c] = "address"
        elif lc in {"url", "link"}:
            rename[c] = "url"
        elif lc in {"rating", "score"}:
            rename[c] = "rating"
        elif lc in {"duration", "stay", "stay_time"}:
            rename[c] = "duration"
        elif lc in {"season", "best_season"}:
            rename[c] = "season"
        elif lc in {"ticket", "price"}:
            rename[c] = "ticket"
        elif lc in {"tips", "tip"}:
            rename[c] = "tips"

    df = df.rename(columns=rename)
    # ensure all columns present
    for col in STANDARD_COLS:
        if col not in df.columns:
            df[col] = None
    return df[STANDARD_COLS]


def sha1_row(row: pd.Series) -> str:
    h = hashlib.sha1()
    key = f"{row.get('city','')}-{row.get('poi_name','')}-{row.get('address','')}-{row.get('url','')}".encode("utf-8", errors="ignore")
    h.update(key)
    return h.hexdigest()


def main():
    citydata_dir = os.environ.get("CITYDATA_DIR", r"C:\\Users\\Administrator\\Desktop\\archive\\citydata")
    if not os.path.isdir(citydata_dir):
        print(f"CITYDATA_DIR not found: {citydata_dir}", file=sys.stderr)
        sys.exit(1)

    csv_files = sorted(glob.glob(os.path.join(citydata_dir, "*.csv")))
    if not csv_files:
        print("No CSV files found in CITYDATA_DIR", file=sys.stderr)
        sys.exit(2)

    frames = []
    for f in csv_files:
        try:
            df = pd.read_csv(f, encoding="utf-8", on_bad_lines="skip")
        except UnicodeDecodeError:
            df = pd.read_csv(f, encoding="gb18030", on_bad_lines="skip")
        df = normalize_columns(df)
        # add province/city from filename when missing
        base = os.path.basename(f)
        city_guess = os.path.splitext(base)[0]
        df["city"] = df["city"].fillna(city_guess)
        frames.append(df)

    all_df = pd.concat(frames, ignore_index=True)
    # drop duplicates by sha1
    all_df["_key"] = all_df.apply(sha1_row, axis=1)
    all_df = all_df.drop_duplicates(subset=["_key"]).drop(columns=["_key"]) 

    out_dir = Path(__file__).parent / "output"
    out_dir.mkdir(parents=True, exist_ok=True)
    out_file = out_dir / "poi_normalized.csv"
    all_df.to_csv(out_file, index=False, encoding="utf-8")
    print(f"Wrote {len(all_df)} rows → {out_file}")


if __name__ == "__main__":
    main()


