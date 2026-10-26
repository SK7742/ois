#!/usr/bin/env python3
"""
===================================================================================
OXFORD INTERNATIONAL SCHOOL - CSMS SUPERUSER CONFIGURATION TOOL
===================================================================================
Configures and initializes the primary institutional Superuser Administrator
account directly in the datastore (PostgreSQL / Supabase or MySQL) with secure
BCrypt password hashing.
===================================================================================
"""

import sys
import os
import argparse
import getpass
from datetime import datetime

try:
    import bcrypt
except ImportError:
    print("[ERROR] Python 'bcrypt' package is missing.")
    print("Please run: pip install bcrypt")
    sys.exit(1)


def parse_args():
    parser = argparse.ArgumentParser(
        description="Configure Institutional Superuser Admin for Oxford CSMS",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Interactive mode (prompts for password securely):
  python configure_superuser.py

  # Non-interactive CLI mode:
  python configure_superuser.py --username admin --password "StrongAdminPass@2026"

  # Custom admin identity:
  python configure_superuser.py --username admin --name "Anil Kumar Gupt" --email "admin@oxford.edu" --phone "+91-7870335141"
        """
    )
    parser.add_argument("-u", "--username", default="admin", help="Superuser username (default: admin)")
    parser.add_argument("-p", "--password", default=None, help="Superuser password (prompted securely if omitted)")
    parser.add_argument("-n", "--name", default="System Administrator", help="Full display name (default: System Administrator)")
    parser.add_argument("-e", "--email", default="admin@oxford.edu", help="Admin email address (default: admin@oxford.edu)")
    parser.add_argument("--phone", default="+91-7870335141", help="Contact phone number (default: +91-7870335141)")
    parser.add_argument("--db-host", default=os.getenv("SPRING_DATASOURCE_HOST", "aws-0-ap-northeast-1.pooler.supabase.com"), help="DB host (default: aws-0-ap-northeast-1.pooler.supabase.com)")
    parser.add_argument("--db-port", type=int, default=int(os.getenv("SPRING_DATASOURCE_PORT", "5432")), help="DB port (default: 5432)")
    parser.add_argument("--db-user", default=os.getenv("SPRING_DATASOURCE_USERNAME", "postgres.byjfotbnqblbkbqdmxsw"), help="DB user (default: postgres.byjfotbnqblbkbqdmxsw)")
    parser.add_argument("--db-pass", default=os.getenv("SPRING_DATASOURCE_PASSWORD", "ois-db@123#"), help="DB password (default: ois-db@123#)")
    parser.add_argument("--db-name", default=os.getenv("SPRING_DATASOURCE_DATABASE", "postgres"), help="DB name (default: postgres)")
    parser.add_argument("-y", "--yes", action="store_true", help="Non-interactive confirmation")
    return parser.parse_args()


def get_db_connection(args):
    # Determine if PostgreSQL or MySQL based on port/host
    is_postgres = (args.db_port == 5432 or "supabase" in args.db_host.lower() or "postgres" in args.db_user.lower())
    if is_postgres:
        try:
            import psycopg2
            from psycopg2.extras import RealDictCursor
            conn = psycopg2.connect(
                host=args.db_host,
                port=args.db_port,
                user=args.db_user,
                password=args.db_pass,
                dbname=args.db_name,
                sslmode="require",
                cursor_factory=RealDictCursor
            )
            return conn, "POSTGRESQL"
        except ImportError:
            print("[ERROR] 'psycopg2' is required for PostgreSQL connections.")
            print("Please run: pip install psycopg2-binary")
            sys.exit(1)
        except Exception as err:
            print(f"\n[FATAL] PostgreSQL connection failed to {args.db_user}@{args.db_host}:{args.db_port}/{args.db_name}")
            print(f"Details: {err}")
            sys.exit(1)
    else:
        try:
            import mysql.connector
            conn = mysql.connector.connect(
                host=args.db_host,
                port=args.db_port,
                user=args.db_user,
                password=args.db_pass,
                database=args.db_name,
                autocommit=False
            )
            return conn, "MYSQL"
        except Exception as err:
            print(f"\n[FATAL] MySQL connection failed to {args.db_user}@{args.db_host}:{args.db_port}/{args.db_name}")
            print(f"Details: {err}")
            sys.exit(1)


def hash_password(plain_password: str) -> str:
    salt = bcrypt.gensalt(rounds=10, prefix=b"2a")
    hashed = bcrypt.hashpw(plain_password.encode("utf-8"), salt)
    return hashed.decode("utf-8")


def main():
    args = parse_args()

    print("=" * 80)
    print("       OXFORD INTERNATIONAL SCHOOL - CSMS SUPERUSER CONFIGURATION")
    print("=" * 80)
    print(" Institution : Oxford International School (Harpur, Raxaul, Bihar)")
    print(" Target DB   : %s@%s:%d/%s" % (args.db_user, args.db_host, args.db_port, args.db_name))
    print("=" * 80)

    username = args.username.strip()
    if not username:
        print("[ERROR] Username cannot be empty.")
        sys.exit(1)

    full_name = args.name.strip()
    email = args.email.strip()
    phone = args.phone.strip()

    # Password resolution: prompt if not provided in CLI
    password = args.password
    if not password:
        print("\n[!] Superuser password was not specified on CLI.")
        print("    Please enter the new superuser password below:")
        while True:
            try:
                pwd1 = getpass.getpass("    Enter Superuser Password: ")
                if len(pwd1) < 6:
                    print("    [!] Password must be at least 6 characters. Please try again.")
                    continue
                pwd2 = getpass.getpass("    Confirm Superuser Password: ")
                if pwd1 != pwd2:
                    print("    [!] Passwords do not match. Please try again.")
                    continue
                password = pwd1
                break
            except (KeyboardInterrupt, EOFError):
                print("\n[ABORTED] Operation cancelled by user.")
                sys.exit(1)
    else:
        if len(password) < 6:
            print("[ERROR] Password must be at least 6 characters long.")
            sys.exit(1)

    print("\n[1/3] Computing secure BCrypt password hash (Cost Factor: 10)...")
    hashed_password = hash_password(password)

    print("[2/3] Connecting to institutional database...")
    conn, engine = get_db_connection(args)
    cursor = conn.cursor()

    try:
        now = datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")

        # Check existing user
        cursor.execute("SELECT id, username, email FROM users WHERE LOWER(username) = LOWER(%s)", (username,))
        existing_user = cursor.fetchone()

        if existing_user:
            user_id = existing_user["id"]
            print(f"[3/3] Updating existing Superuser account ID #{user_id} ('{username}')...")
            enabled_val = True if engine == "POSTGRESQL" else b'1'
            cursor.execute("""
                UPDATE users
                SET password = %s,
                    full_name = %s,
                    email = %s,
                    phone = %s,
                    enabled = %s,
                    updated_at = %s
                WHERE id = %s
            """, (hashed_password, full_name, email, phone, enabled_val, now, user_id))
        else:
            print(f"[3/3] Creating new Superuser account for '{username}'...")
            enabled_val = True if engine == "POSTGRESQL" else b'1'
            if engine == "POSTGRESQL":
                cursor.execute("""
                    INSERT INTO users (username, password, full_name, email, phone, enabled, created_at, updated_at)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                    RETURNING id
                """, (username, hashed_password, full_name, email, phone, enabled_val, now, now))
                user_id = cursor.fetchone()["id"]
            else:
                cursor.execute("""
                    INSERT INTO users (username, password, full_name, email, phone, enabled, created_at, updated_at)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                """, (username, hashed_password, full_name, email, phone, enabled_val, now, now))
                user_id = cursor.lastrowid

        # Update roles: Ensure ROLE_ADMIN and ROLE_MANAGEMENT are present
        cursor.execute("SELECT role FROM user_roles WHERE user_id = %s", (user_id,))
        current_roles = {row["role"] for row in cursor.fetchall()}

        required_roles = ["ROLE_ADMIN", "ROLE_MANAGEMENT"]
        for role in required_roles:
            if role not in current_roles:
                cursor.execute("INSERT INTO user_roles (user_id, role) VALUES (%s, %s)", (user_id, role))

        conn.commit()

        print("\n" + "=" * 80)
        print("          SUPERUSER ADMINISTRATOR CONFIGURATION SUCCESSFUL!")
        print("=" * 80)
        print("  Database     : %s (Supabase / Cloud)" % engine)
        print("  Superuser ID : #%d" % user_id)
        print("  Username     : %s" % username)
        print("  Full Name    : %s" % full_name)
        print("  Email        : %s" % email)
        print("  Phone        : %s" % phone)
        print("  Account State: ACTIVE (Enabled: Yes)")
        print("  Assigned Role: ROLE_ADMIN, ROLE_MANAGEMENT (Superuser)")
        print("  Security     : BCrypt Hashed (Length: %d characters)" % len(hashed_password))
        print("  Login URL    : http://localhost:8080/")
        print("=" * 80)
        print("Note: Login credentials are not printed to logs or stdout for security.\n")

    except Exception as e:
        conn.rollback()
        print(f"\n[ERROR] Failed to save superuser configuration: {e}")
        sys.exit(1)
    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":
    main()
