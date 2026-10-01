#!/usr/bin/env python3
"""Bundle the addon jars in libs/addons/ into the built client jar as jar-in-jar.

Run AFTER `./gradlew build`:

    ./gradlew build
    python scripts/bundle_addons.py

Produces build/libs/star-client-<mcversion>.jar with each addon nested under
META-INF/jars/ and registered in the mod's fabric.mod.json "jars" array (added
alongside the client's own bundled libraries). Loom's own `include` does not
handle these prebuilt third-party jars cleanly, so we nest them here instead.
"""
import glob, json, os, sys, zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LIBS = os.path.join(ROOT, "libs", "addons")
LIBDIR = os.path.join(ROOT, "build", "libs")

# the client jar built by gradle (id stays meteor-client, hence this name)
cands = [j for j in glob.glob(os.path.join(LIBDIR, "meteor-client-*.jar"))
         if "-sources" not in j and "-javadoc" not in j]
if not cands:
    sys.exit("No built client jar found in build/libs (run ./gradlew build first).")
src_jar = max(cands, key=os.path.getmtime)

addons = sorted(glob.glob(os.path.join(LIBS, "*.jar")))
if not addons:
    sys.exit("No addon jars found in libs/addons/.")

# derive output name from the mc version in the jar filename
base = os.path.basename(src_jar)                     # meteor-client-<ver>.jar
ver = base[len("meteor-client-"):-len(".jar")]
out_jar = os.path.join(LIBDIR, f"star-client-{ver}.jar")

with zipfile.ZipFile(src_jar, "r") as z:
    fmj = json.loads(z.read("fabric.mod.json"))

existing = fmj.get("jars", [])
existing_files = {j["file"] for j in existing}

entries = []
for a in addons:
    dest = "META-INF/jars/" + os.path.basename(a)
    if dest in existing_files:
        dest = "META-INF/jars/addon-" + os.path.basename(a)
    entries.append((a, dest))

fmj["jars"] = existing + [{"file": d} for _, d in entries]

tmp = out_jar + ".tmp"
with zipfile.ZipFile(src_jar, "r") as zin, \
     zipfile.ZipFile(tmp, "w", zipfile.ZIP_DEFLATED) as zout:
    for item in zin.infolist():
        if item.filename == "fabric.mod.json":
            continue
        zout.writestr(item, zin.read(item.filename))
    zout.writestr("fabric.mod.json", json.dumps(fmj, indent=2, ensure_ascii=False))
    for src, dest in entries:
        with open(src, "rb") as f:
            zout.writestr(dest, f.read())

if os.path.exists(out_jar):
    os.remove(out_jar)
os.replace(tmp, out_jar)
print("Wrote", out_jar)
for _, d in entries:
    print("  nested", d)
