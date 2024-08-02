import ws from "k6/ws";
import { check, sleep } from "k6";

const PROTOCOL = "ws";
const CLUSTER_FQDN = "ec2-44-214-107-128.compute-1.amazonaws.com";
const PORT = 31699;
const ENDPOINT = "/api/ws";

// Define questions and chat IDs for testing
const questions = [
    { question: "De que se trata la materia de DeepLearning?", chatId: 1 },
    { question: "De qué es neurociencia computacional?", chatId: 1 },
    { question: "Es difícil neurociencia computacional?", chatId: 1 },
];

export let options = {
    stages: [
        { duration: "15s", target: 10 }, // Ramp up to 10 VUs
        { duration: "30s", target: 10 }, // Stay at 10 VUs
        { duration: "15s", target: 0 }, // Ramp down to 0 VUs
    ],
};

const url = `${PROTOCOL}://${CLUSTER_FQDN}:${PORT}${ENDPOINT}`;

export default function () {
    // Open WebSocket connection
    const res = ws.connect(url, null, function (socket) {
        socket.on("open", () => console.log("WebSocket connected"));

        // Choose a random question and chatId
        const payload = questions[Math.floor(Math.random() * questions.length)];

        // Send payload
        socket.send(
            JSON.stringify({
                type: "question",
                question: payload.question,
                chatId: payload.chatId,
            })
        );

        // Receive response
        socket.on("message", (message) => {
            console.log(`Received message: ${message}`);
        });

        // Close the WebSocket connection after 1 second
        sleep(1);
        socket.close();
    });

    // Optional: Handle connection errors
    check(res, { "status is 101": (r) => r && r.status === 101 });

    sleep(1); // Wait a bit before the next iteration
}
