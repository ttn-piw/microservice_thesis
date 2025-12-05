import { sendMessage } from "../api/chatbotService.js"; 

document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('Bearer');

    if (!token || token === 'null' || token === 'undefined' || token.trim() === "") {
        localStorage.removeItem('Bearer'); 
        localStorage.removeItem('userEmail');

        alert("You need to log in to use this feature!");
        
        window.location.replace("/pages/login.html"); 
        
        return;
    }

    const elements = {
        toggleBtn: document.getElementById('toggle-chat'),
        closeBtn: document.getElementById('close-chat'),
        chatWidget: document.getElementById('chat-widget'),
        chatWindow: document.getElementById('chat-window'),
        chatForm: document.getElementById('chat-form'),
        chatInput: document.getElementById('chat-input'),
        messagesContainer: document.getElementById('chat-messages'),
        loadingIndicator: document.getElementById('chat-loading')
    };

    if (!elements.toggleBtn || !elements.chatWindow) {
        console.error("Chat elements not found!");
        return;
    }
    console.log("Chatbot loaded successfully");

    const ui = {
        toggleChat() {
            const isHidden = elements.chatWindow.classList.contains('hidden');
            
            if (isHidden) {
                elements.chatWindow.classList.remove('hidden');
                setTimeout(() => {
                    elements.chatWindow.classList.remove('scale-95', 'opacity-0');
                    elements.chatWindow.classList.add('scale-100', 'opacity-100');
                    elements.chatInput.focus();
                }, 10);
            } else {
                elements.chatWindow.classList.remove('scale-100', 'opacity-100');
                elements.chatWindow.classList.add('scale-95', 'opacity-0');
                setTimeout(() => {
                    elements.chatWindow.classList.add('hidden');
                }, 300); 
            }
        },

        scrollToBottom() {
            elements.messagesContainer.scrollTop = elements.messagesContainer.scrollHeight;
        },

        addMessage(text, sender) {
            const div = document.createElement('div');
            const isUser = sender === 'user';
            
            // Use flexbox for alignment
            div.className = `flex ${isUser ? 'justify-end' : 'justify-start'} animate-fade-in`;
            
           // Handle text null or undefined
            const safeText = text ? text : "";
            const formattedText = safeText.replace(/\n/g, '<br>');

            div.innerHTML = `
                <div class="${isUser 
                    ? 'bg-blue-600 text-white rounded-tr-none' 
                    : 'bg-white border border-gray-200 text-gray-800 rounded-tl-none'} 
                    rounded-2xl py-2 px-4 shadow-sm max-w-[85%] text-sm break-words">
                    ${formattedText}
                </div>
            `;
            
            elements.messagesContainer.appendChild(div);
            this.scrollToBottom();
        },

        showLoading(show) {
            if (show) {
                elements.loadingIndicator.classList.remove('hidden');
                this.scrollToBottom();
            } else {
                elements.loadingIndicator.classList.add('hidden');
            }
        }
    };

    elements.toggleBtn.addEventListener('click', () => {
        console.log("Toggle button clicked");
        ui.toggleChat();
    });

    elements.closeBtn.addEventListener('click', ui.toggleChat);

    elements.chatForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const message = elements.chatInput.value.trim();
        if (!message) return;

        ui.addMessage(message, 'user');
        elements.chatInput.value = '';
        ui.showLoading(true);

        try {
            const responseData = await sendMessage(message);
            const botReply = responseData.response || responseData.content || responseData.generation || "Không có phản hồi.";

            ui.showLoading(false);
            ui.addMessage(botReply, 'bot');

        } catch (error) {
            console.error(error);
            ui.showLoading(false);
            ui.addMessage("Sorry, the system is busy. Please try again later.", 'bot');
        }
    });
});