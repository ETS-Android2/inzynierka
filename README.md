Android application made for a engineering work.
------------------
Workflow :
- User creates company accounts, system automatically generates X amount of company members accounts and also theirs QR codes stored on device
- QR codes generete :
  - System from inputted secret performs Adi Shamir algorithm, then after division use AES cipher on each one and finally generate QR code
- User loged to app can make request to decrypt company secret, for that he has to send his own QR code, then everyone who are at least once open the app (unique tokens from Firebase for every devices) get notification. 
- If specific user belongs to this company, can send own QR. After passing minimal threshold, secret will appear on app
