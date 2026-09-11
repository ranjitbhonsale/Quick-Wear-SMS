import os
import sys
import json
from google.oauth2 import service_account
from googleapiclient.discovery import build
from googleapiclient.http import MediaFileUpload

def main():
    json_key_str = os.environ.get("PLAY_STORE_JSON_KEY")
    if not json_key_str:
        print("PLAY_STORE_JSON_KEY environment variable not found. Skipping Play Store deployment.")
        return

    package_name = "work.ranjit.quicksmswear"
    aab_path = sys.argv[1] if len(sys.argv) > 1 else "app/build/outputs/bundle/release/app-release.aab"

    if not os.path.exists(aab_path):
        print(f"Error: AAB file not found at {aab_path}")
        sys.exit(1)

    try:
        credentials_info = json.loads(json_key_str)
        credentials = service_account.Credentials.from_service_account_info(
            credentials_info,
            scopes=['https://www.googleapis.com/auth/androidpublisher']
        )

        service = build('androidpublisher', 'v3', credentials=credentials)
        edits = service.edits()

        print(f"Creating edit session for package: {package_name}")
        edit_response = edits.insert(body={}, packageName=package_name).execute()
        edit_id = edit_response['id']
        print(f"Created edit ID: {edit_id}")

        print(f"Uploading AAB bundle: {aab_path}")
        media = MediaFileUpload(aab_path, mimetype='application/octet-stream', resumable=True)
        bundle_response = edits.bundles().upload(
            packageName=package_name,
            editId=edit_id,
            media_body=media
        ).execute()

        version_code = bundle_response['versionCode']
        print(f"Uploaded AAB Version Code: {version_code}")

        print(f"Assigning version {version_code} to 'internal' track...")
        edits.tracks().update(
            packageName=package_name,
            editId=edit_id,
            track='internal',
            body={
                'releases': [{
                    'versionCodes': [str(version_code)],
                    'status': 'completed'
                }]
            }
        ).execute()

        print("Committing edit session to Google Play Console...")
        edits.commit(packageName=package_name, editId=edit_id).execute()
        print("Successfully published release to Google Play Internal Track!")

    except Exception as e:
        print(f"Error publishing to Google Play: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
