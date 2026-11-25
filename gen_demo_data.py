#!/usr/bin/env python3
"""Generate demo SQL: 3 Génie Civil students + accepted CVs + accepted applications for offer id=5."""

import io, textwrap

BIB_HASH = "$2a$10$hk7OcuG7MdnCkqD/KYR8HuF1dRUYiWkvmFx65wN1mwtr.22VPMVWW"
OFFER_ID  = 5
PROGRAME  = 2   # TECHNIQUE_GENIE_CIVIL ordinal

# ── Minimal PDF generator ───────────────────────────────────────────────────

def make_pdf(lines: list[str]) -> bytes:
    """Return bytes of a minimal valid PDF containing the given text lines."""
    content_lines = []
    y = 750
    content_lines.append("BT")
    content_lines.append("/F1 11 Tf")
    for line in lines:
        safe = line.replace("\\","\\\\").replace("(","\\(").replace(")","\\)").replace("\n","")
        content_lines.append(f"72 {y} Td")
        content_lines.append(f"({safe}) Tj")
        content_lines.append("0 -16 Td")
        y -= 16
    content_lines.append("ET")
    stream = "\n".join(content_lines).encode("latin-1", errors="replace")

    def obj(n, d, stream_bytes=None):
        s = f"{n} 0 obj\n{d}\n"
        if stream_bytes is not None:
            s += f"stream\n"
            return s.encode() + stream_bytes + b"\nendstream\nendobj\n"
        return (s + "endobj\n").encode()

    objects = []
    offsets = []
    buf = io.BytesIO()

    header = b"%PDF-1.4\n"
    buf.write(header)

    def write_obj(n, d, sb=None):
        offsets.append(buf.tell())
        buf.write(obj(n, d, sb))

    write_obj(1, "<< /Type /Catalog /Pages 2 0 R >>")
    write_obj(2, "<< /Type /Pages /Kids [3 0 R] /Count 1 >>")
    write_obj(3, f"<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>")
    write_obj(4, f"<< /Length {len(stream)} >>", stream)
    write_obj(5, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>")

    xref_offset = buf.tell()
    buf.write(b"xref\n")
    buf.write(f"0 {len(offsets)+1}\n".encode())
    buf.write(b"0000000000 65535 f \n")
    for off in offsets:
        buf.write(f"{off:010d} 00000 n \n".encode())
    buf.write(b"trailer\n")
    buf.write(f"<< /Size {len(offsets)+1} /Root 1 0 R >>\n".encode())
    buf.write(b"startxref\n")
    buf.write(f"{xref_offset}\n".encode())
    buf.write(b"%%EOF\n")
    return buf.getvalue()

# ── CV content ──────────────────────────────────────────────────────────────

cvs = {
    "alice": make_pdf([
        "CURRICULUM VITAE",
        "",
        "Alice Bouchard",
        "alice.bouchard@etudiant.ca  |  514-555-0101  |  Montreal, QC",
        "",
        "OBJECTIF",
        "Stage en genie civil - conception et inspection d'infrastructures",
        "",
        "FORMATION",
        "Diplome d'etudes collegiales - Technique de genie civil (221.B0)",
        "College de Rosemont, Montreal  |  2022-2025 (en cours)",
        "Cours pertinents: Topographie, Mecanique des sols, Beton arme,",
        "  Dessin de plans, Hydraulique, Estimation et devis",
        "",
        "EXPERIENCE",
        "Aide-technicienne en chantier - Construction Belanger Inc.  (ete 2024)",
        "  - Releves topographiques au total station et niveau optique",
        "  - Controle qualite du beton (affaissement, prise d'eprouvettes)",
        "  - Suivi des plans d'execution et mise a jour des as-built",
        "  - Redaction de rapports journaliers de chantier",
        "",
        "Assistante technique - Arrondissement Rosemont (ete 2023)",
        "  - Inspection visuelle de trottoirs, bordures et regards",
        "  - Compilation des donnees d'inspection dans Excel",
        "",
        "COMPETENCES TECHNIQUES",
        "AutoCAD Civil 3D, Total Station Leica, niveau optique, GPS RTK",
        "Essais de beton (ASTM C143, C39), analyse granulometrique",
        "Microsoft Office (Excel, Word), lecture de plans et devis",
        "",
        "LANGUES",
        "Francais (langue maternelle)  |  Anglais (intermediaire - B2)",
    ]),
    "bruno": make_pdf([
        "CURRICULUM VITAE",
        "",
        "Bruno Lacroix",
        "bruno.lacroix@etudiant.ca  |  438-555-0202  |  Laval, QC",
        "",
        "OBJECTIF",
        "Stage en genie civil - infrastructures routieres et municipales",
        "",
        "FORMATION",
        "Diplome d'etudes collegiales - Technique de genie civil (221.B0)",
        "College Montmorency, Laval  |  2022-2025 (en cours)",
        "Cours pertinents: Voirie et infrastructures, Drainage urbain,",
        "  Resistance des materiaux, Gestion de projets, DAO",
        "",
        "EXPERIENCE",
        "Technicien junior - Routek Infrastructures Inc.  (ete 2024)",
        "  - Inspection des ouvrages routiers (fissures, nids-de-poule, deformations)",
        "  - Collecte et saisie de donnees terrain avec tablette Trimble",
        "  - Preparation de bordereaux de quantites pour estimations",
        "  - Participation aux reunions de chantier et suivi des travaux",
        "",
        "Manoeuvre specialise - Ville de Laval  (ete 2023)",
        "  - Travaux de rehabilitation de conduites d'aqueduc",
        "  - Pose de bordures et dalles en beton",
        "",
        "COMPETENCES TECHNIQUES",
        "AutoCAD 2D/3D, MicroStation, Civil 3D (notions)",
        "Essais Proctor, granulometrie, limites d'Atterberg",
        "Lecture de plans municipaux et routiers, devis CCDG",
        "Trimble Access, Microsoft Project",
        "",
        "LANGUES",
        "Francais (langue maternelle)  |  Anglais (fonctionnel - B1)",
    ]),
    "clara": make_pdf([
        "CURRICULUM VITAE",
        "",
        "Clara Morin",
        "clara.morin@etudiant.ca  |  450-555-0303  |  Brossard, QC",
        "",
        "OBJECTIF",
        "Stage en genie civil - batiment et structures",
        "",
        "FORMATION",
        "Diplome d'etudes collegiales - Technique de genie civil (221.B0)",
        "College Edouard-Montpetit, Longueuil  |  2022-2025 (en cours)",
        "Cours pertinents: Structures en acier et beton, Fondations,",
        "  Thermique du batiment, Mécanique des fluides, BIM",
        "",
        "EXPERIENCE",
        "Assistante en bureau d'etudes - Groupe SNC-Lavalin  (2023)",
        "  - Mise a jour de plans architecturaux et structuraux sous AutoCAD",
        "  - Calcul de charges (neige, vent, sismique) selon le CNBC 2020",
        "  - Verification de conformite aux normes NQ et BNQ",
        "  - Preparation de fiches techniques et cahiers de charge",
        "",
        "Stagiaire en verification - Bureau CBCQ  (ete 2023)",
        "  - Inspection de batiments residentiels (fondations, enveloppe)",
        "  - Redaction de rapports d'inspection avec photos annotees",
        "",
        "COMPETENCES TECHNIQUES",
        "AutoCAD Civil 3D, Revit (BIM niveau 2), Robot Structural Analysis",
        "Codes: CNBC 2020, NQ 3660-500, CSA A23.3 (beton), CSA S16 (acier)",
        "Essais NDT (sclerometre, pachometre), inspection visuelle",
        "Microsoft Office, Bluebeam Revu",
        "",
        "LANGUES",
        "Francais (langue maternelle)  |  Anglais (avance - C1)",
    ]),
}

# ── SQL generation ──────────────────────────────────────────────────────────

def pg_bytea(b: bytes) -> str:
    return "\\x" + b.hex()

students = [
    dict(first="Alice",  last="Bouchard", email="alice.bouchard@etudiant.ca", mat="1234567", cv_key="alice"),
    dict(first="Bruno",  last="Lacroix",  email="bruno.lacroix@etudiant.ca",  mat="2345678", cv_key="bruno"),
    dict(first="Clara",  last="Morin",    email="clara.morin@etudiant.ca",    mat="3456789", cv_key="clara"),
]

sql = []
sql.append("BEGIN;")
sql.append("")
sql.append("-- 0. Reset offer to ACCEPTED + 2 positions")
sql.append(f"UPDATE job_offer SET status = 'ACCEPTED', number_of_positions = 2 WHERE id = {OFFER_ID};")
sql.append("")
sql.append("-- 0b. Reset all applications for this offer back to ACCEPTED")
sql.append(f"UPDATE job_applications SET status = 'ACCEPTED' WHERE job_offer_id = {OFFER_ID};")
sql.append("")
sql.append("-- 1. Users (insert if not exists)")
for s in students:
    sql.append(
        f"INSERT INTO users (email, first_name, last_name, password, role) "
        f"VALUES ('{s['email']}', '{s['first']}', '{s['last']}', '{BIB_HASH}', 'STUDENT') "
        f"ON CONFLICT (email) DO NOTHING;"
    )
sql.append("")
sql.append("-- 2. Students (insert if not exists)")
for s in students:
    sql.append(
        f"INSERT INTO student (id, matricule, programe) "
        f"SELECT id, '{s['mat']}', {PROGRAME} FROM users WHERE email = '{s['email']}' "
        f"ON CONFLICT (id) DO NOTHING;"
    )
sql.append("")
sql.append("-- 3. CVs (update content if exists, insert if not)")
for s in students:
    cv_bytes  = cvs[s["cv_key"]]
    hex_val   = pg_bytea(cv_bytes)
    cv_key    = s["cv_key"]
    fname     = f"cv_{cv_key}.pdf"
    email     = s["email"]
    size      = len(cv_bytes)
    sql.append(
        f"INSERT INTO student_cvs (student_id, file_name, content_type, content, size, status, uploaded_at) "
        f"SELECT id, '{fname}', 'application/pdf', '{hex_val}', {size}, 'ACCEPTED', NOW() "
        f"FROM users WHERE email = '{email}' "
        f"ON CONFLICT DO NOTHING;"
    )
    sql.append(
        f"UPDATE student_cvs SET content = '{hex_val}', size = {size}, file_name = '{fname}', status = 'ACCEPTED' "
        f"WHERE student_id = (SELECT id FROM users WHERE email = '{email}');"
    )
sql.append("")
sql.append("-- 4. Applications (insert if not exists)")
for s in students:
    sql.append(
        f"INSERT INTO job_applications (job_offer_id, student_cv_id, status, applied_at) "
        f"SELECT {OFFER_ID}, sc.id, 'ACCEPTED', NOW() "
        f"FROM student_cvs sc JOIN users u ON sc.student_id = u.id "
        f"WHERE u.email = '{s['email']}' "
        f"ON CONFLICT DO NOTHING;"
    )
sql.append("")
sql.append("COMMIT;")

with open("demo_data.sql", "w") as f:
    f.write("\n".join(sql))

print("demo_data.sql written.")
print(f"Alice CV size:  {len(cvs['alice'])} bytes")
print(f"Bruno CV size:  {len(cvs['bruno'])} bytes")
print(f"Clara CV size:  {len(cvs['clara'])} bytes")
