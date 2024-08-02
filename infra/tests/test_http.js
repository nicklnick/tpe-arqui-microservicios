import http from "k6/http";
import { check, sleep } from "k6";

const PROTOCOL = "http";
const CLUSTER_FQDN = "ec2-44-214-107-128.compute-1.amazonaws.com";
const PORT = 31699;
const ENDPOINT = "/api/users";

// Define your options with multiple scenarios
export let options = {
    scenarios: {
        my_scenario: {
            executor: "ramping-vus",
            startVUs: 1,
            stages: [
                { duration: "15s", target: 10 }, // Ramp up to 10 VUs in 15 seconds
                { duration: "30s", target: 10 }, // Stay at 10 VUs for 30 seconds
                { duration: "15s", target: 0 }, // Ramp down to 0 VUs in 15 seconds
            ],
            gracefulStop: "10s", // Allow 10 seconds for graceful stop
        },
    },
};

const url = `${PROTOCOL}://${CLUSTER_FQDN}:${PORT}${ENDPOINT}`;

console.log(`URL: ${url}`);

export default function () {
    const headers = { "Content-Type": "application/json" };

    let res = http.get(url, { headers: headers });
    check(res, {
        "is status 400": (r) => r.status === 400,
    });
    sleep(1);
}