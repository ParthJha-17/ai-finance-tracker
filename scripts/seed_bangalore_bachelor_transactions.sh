#!/usr/bin/env bash
set -euo pipefail

API_URL="${API_URL:-http://localhost:8080}"
USER_ID="${USER_ID:-}"

if [[ -z "${USER_ID}" ]]; then
  user_payload='{"name":"Bangalore Bachelor","email":"bachelor@example.com"}'
  user_response=$(curl -s -X POST "${API_URL}/api/users" \
    -H "Content-Type: application/json" \
    -d "${user_payload}")
  USER_ID=$(python - <<'PY'
import json
import sys

data = json.load(sys.stdin)
print(data["id"])
PY
  <<< "${user_response}")
fi

add_transaction() {
  local category="$1"
  local description="$2"
  local amount="$3"
  local type="$4"
  local date="$5"

  curl -s -X POST "${API_URL}/api/transactions/${USER_ID}" \
    -H "Content-Type: application/json" \
    -d "{\"category\":\"${category}\",\"description\":\"${description}\",\"amount\":${amount},\"type\":\"${type}\",\"date\":\"${date}\"}" \
    > /dev/null
}

transactions=$(cat <<'EOF'
Income|Monthly salary|70000|INCOME|2025-02-01
Housing & Utilities|Rent for 1BHK Koramangala|18000|EXPENSE|2025-02-01
Housing & Utilities|Electricity bill BESCOM|1200|EXPENSE|2025-02-02
Housing & Utilities|Broadband internet plan|999|EXPENSE|2025-02-02
Housing & Utilities|Mobile postpaid bill|499|EXPENSE|2025-02-03
Groceries|Monthly grocery run at BigBasket|3200|EXPENSE|2025-02-03
Groceries|Milk, fruits, veggies|450|EXPENSE|2025-02-03
Dining & Entertainment|Zomato dinner|380|EXPENSE|2025-02-04
Dining & Entertainment|Swiggy lunch|260|EXPENSE|2025-02-04
Dining & Entertainment|Coffee at cafe|180|EXPENSE|2025-02-04
Transportation|Metro card recharge|600|EXPENSE|2025-02-05
Transportation|Auto ride to office|150|EXPENSE|2025-02-05
Transportation|Uber ride home|320|EXPENSE|2025-02-05
Transportation|Fuel for bike|1200|EXPENSE|2025-02-06
Transportation|Bike service|800|EXPENSE|2025-02-06
Personal & Lifestyle|Gym membership|1500|EXPENSE|2025-02-06
Dining & Entertainment|Netflix subscription|649|EXPENSE|2025-02-07
Dining & Entertainment|Spotify subscription|119|EXPENSE|2025-02-07
Dining & Entertainment|Movie tickets|500|EXPENSE|2025-02-07
Dining & Entertainment|Weekend bar outing|1200|EXPENSE|2025-02-08
Personal & Lifestyle|Laundry service|350|EXPENSE|2025-02-08
Personal & Lifestyle|Haircut|300|EXPENSE|2025-02-09
Personal & Lifestyle|Skincare products|650|EXPENSE|2025-02-09
Healthcare & Insurance|Pharmacy purchase|280|EXPENSE|2025-02-10
Healthcare & Insurance|Doctor consultation|600|EXPENSE|2025-02-10
Healthcare & Insurance|Health insurance premium|1200|EXPENSE|2025-02-11
Transportation|Bike insurance|1800|EXPENSE|2025-02-11
Transportation|Parking fee|200|EXPENSE|2025-02-12
Transportation|Toll charges|120|EXPENSE|2025-02-12
Dining & Entertainment|Office lunch|220|EXPENSE|2025-02-13
Dining & Entertainment|Evening snacks|140|EXPENSE|2025-02-13
Education|Tech book purchase|550|EXPENSE|2025-02-14
Education|Online course subscription|1200|EXPENSE|2025-02-14
Travel|Flight ticket to home|6500|EXPENSE|2025-02-15
Travel|Hotel stay|2400|EXPENSE|2025-02-15
Travel|Airport cab|600|EXPENSE|2025-02-15
Groceries|Grocery restock|2100|EXPENSE|2025-02-16
Groceries|Vegetable market|300|EXPENSE|2025-02-16
Dining & Entertainment|Dining out with friends|750|EXPENSE|2025-02-17
Personal & Lifestyle|Festival shopping|2300|EXPENSE|2025-02-18
Personal & Lifestyle|Shoes purchase|2500|EXPENSE|2025-02-18
Personal & Lifestyle|Clothing purchase|1800|EXPENSE|2025-02-19
Savings & Investments|Transfer to savings|5000|EXPENSE|2025-02-20
Savings & Investments|Emergency fund contribution|3000|EXPENSE|2025-02-20
Savings & Investments|Extra credit card payoff|2000|EXPENSE|2025-02-21
Groceries|Home supplies|900|EXPENSE|2025-02-21
Housing & Utilities|Water bill|450|EXPENSE|2025-02-22
Housing & Utilities|Gas cylinder refill|1200|EXPENSE|2025-02-22
Transportation|Metro ride|120|EXPENSE|2025-02-23
Transportation|Uber ride to meetup|280|EXPENSE|2025-02-23
EOF
)

while IFS="|" read -r category description amount type date; do
  add_transaction "${category}" "${description}" "${amount}" "${type}" "${date}"
done <<< "${transactions}"

echo "Seeded transactions for user ${USER_ID}."
