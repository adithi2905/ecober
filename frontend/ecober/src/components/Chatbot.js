import React, { useState } from "react";
import axios from "axios";

const ChatBot = () => {
  const [messages, setMessages] = useState([
    { text: "Hi! Ask me about your carbon emissions 🌿", sender: "bot" }
  ]);
  const [input, setInput] = useState("");

  const sendMessage = async () => {
    if (!input.trim()) return;

    const newMessages = [...messages, { text: input, sender: "user" }];
    setMessages(newMessages);

    try {
      const response = await axios.post("/api/chat", { message: input });
      const botReply = response.data.reply;

      setMessages([...newMessages, { text: botReply, sender: "bot" }]);
    } catch (err) {
      setMessages([
        ...newMessages,
        { text: "Oops! Something went wrong.", sender: "bot" }
      ]);
    }

    setInput("");
  };

  return (
    <div className="fixed bottom-6 right-6 w-80 bg-white shadow-lg rounded-2xl overflow-hidden">
      <div className="p-3 bg-green-700 text-white font-bold"> 🌱 Ecober Bot</div>
      <div className="h-64 overflow-y-auto p-2 bg-gray-50">
        {messages.map((msg, i) => (
          <div
            key={i}
            className={`my-2 ${
              msg.sender === "user" ? "text-right" : "text-left text-green-800"
            }`}
          >
            <div
              className={`inline-block px-3 py-2 rounded-xl ${
                msg.sender === "user"
                  ? "bg-green-100"
                  : "bg-green-200"
              }`}
            >
            {msg.text}
            </div>
        </div>
        ))}
    </div>
    <div className="flex border-t">
        <input
        className="flex-1 p-2 outline-none"
        value={input}
        onChange={(e) => setInput(e.target.value)}
        placeholder="Type your message..."
        onKeyDown={(e) => {
            if (e.key === "Enter") sendMessage();
        }}
        />
        <button
            className="bg-green-600 text-white px-4"
            onClick={sendMessage}
        >
        ➤
        </button>
    </div>
    </div>
);
};

export default ChatBot;
