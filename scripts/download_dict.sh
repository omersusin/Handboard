#!/bin/bash
ASSETS_DIR="app/src/main/assets"
mkdir -p "$ASSETS_DIR"

echo "=== HandBoard Dictionary Downloader ==="

# --- English (US) ---
echo "[1/3] Downloading English word list..."
TEMP_FILE=$(mktemp)
curl -sL "https://raw.githubusercontent.com/first20hours/google-10000-english/master/google-10000-english-usa-no-swears.txt" -o "$TEMP_FILE"

if [ ! -s "$TEMP_FILE" ]; then
    echo "  ✗ Download failed."
else
    TOTAL=$(wc -l < "$TEMP_FILE")
    awk -v total="$TOTAL" '{
        freq = total - NR + 1
        if (NR <= 100) freq = freq * 10
        else if (NR <= 500) freq = freq * 5
        else if (NR <= 1000) freq = freq * 3
        else if (NR <= 2000) freq = freq * 2
        gsub(/[^a-zA-Z'\-]/, "", $1)
        if (length($1) >= 1) print tolower($1) "\t" freq
    }' "$TEMP_FILE" > "$ASSETS_DIR/en_us.txt"
    echo "  ✓ Saved to en_us.txt"
fi
rm -f "$TEMP_FILE"

# --- English Bigrams ---
echo "[2/3] Generating bigram data..."
cat > "$ASSETS_DIR/en_us_bigrams.txt" << 'BIGRAMEOF'
i	am	500
i	have	480
i	will	470
you	are	500
it	is	500
we	are	500
they	are	500
that	is	500
this	is	500
what	is	500
how	are	500
do	you	500
don't	know	490
can	you	500
will	be	500
have	to	500
want	to	500
thank	you	500
good	morning	500
see	you	500
let	me	500
of	the	500
in	the	490
BIGRAMEOF

# --- Turkish (Basic) ---
echo "[3/3] Generating Turkish dictionary..."
cat > "$ASSETS_DIR/tr_tr.txt" << 'TREOF'
bir	999
ve	998
bu	997
da	996
de	995
ne	994
ben	993
sen	992
o	991
biz	990
siz	989
onlar	988
var	987
yok	986
için	985
ile	984
ama	983
çok	982
daha	981
en	980
gibi	979
kadar	978
sonra	977
önce	976
şimdi	975
merhaba	949
günaydın	948
nasılsın	943
teşekkür	950
evet	953
hayır	952
lütfen	951
TREOF

echo "=== Done ==="
