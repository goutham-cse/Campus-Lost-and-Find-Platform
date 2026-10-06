// Main JavaScript for Campus Lost & Found Platform

document.addEventListener("DOMContentLoaded", function () {
    const categorySelect = document.getElementById("categorySelect");
    const questionsContainer = document.getElementById("questionsContainer");
    const imageInput = document.getElementById("imageFileInput");
    const imagePreview = document.getElementById("imagePreview");
    const aiStatusBanner = document.getElementById("aiStatusBanner");

    // Dynamic Category Question Templates
    const questionSchemas = {
        "ID_CARD": [
            { key: "nameOnCard", label: "Full Name on ID Card", placeholder: "e.g. Rahul Sharma", type: "text", required: true },
            { key: "department", label: "Department / Branch", placeholder: "e.g. Computer Science", type: "text", required: true },
            { key: "yearSection", label: "Year & Section", placeholder: "e.g. 3rd Year / Sec A", type: "text", required: true },
            { key: "rollNumber", label: "Student / Staff Roll No.", placeholder: "e.g. 21CS105", type: "text", required: true }
        ],
        "ELECTRONICS": [
            { key: "deviceType", label: "Device Type", placeholder: "e.g. Smartphone, Laptop, Wireless Earbuds", type: "text", required: true },
            { key: "brand", label: "Brand / Manufacturer", placeholder: "e.g. Apple, Samsung, Dell, Sony", type: "text", required: true },
            { key: "color", label: "Device Color", placeholder: "e.g. Space Grey, Midnight Blue", type: "text", required: true },
            { key: "modelSerial", label: "Model or Serial/IMEI Number (or distinctive stickers)", placeholder: "e.g. iPhone 13 / Sticker of Marvel logo", type: "text", required: false }
        ],
        "KEYS": [
            { key: "keyCount", label: "Number of Keys", placeholder: "e.g. 3 keys on ring", type: "text", required: true },
            { key: "keychainDescription", label: "Keychain / Key Ring Description", placeholder: "e.g. Red leather tag with 'Honda' logo", type: "text", required: true },
            { key: "keyType", label: "Type of Keys", placeholder: "e.g. Room key, Bike key, Hostel locker key", type: "text", required: true }
        ],
        "WALLET": [
            { key: "color", label: "Wallet Color & Material", placeholder: "e.g. Black Genuine Leather", type: "text", required: true },
            { key: "brand", label: "Brand / Label", placeholder: "e.g. Tommy Hilfiger, Wildhorn", type: "text", required: false },
            { key: "contents", label: "Key Contents (Card names, Cash range, ID inside)", placeholder: "e.g. Driving License of John, Axis Bank Card", type: "text", required: true }
        ],
        "BOOKS_NOTES": [
            { key: "titleSubject", label: "Book Title / Course Subject", placeholder: "e.g. Data Structures & Algorithms", type: "text", required: true },
            { key: "authorDept", label: "Author / Department", placeholder: "e.g. Tanenbaum / ECE Dept", type: "text", required: true },
            { key: "ownerName", label: "Name Written Inside (if any)", placeholder: "e.g. Written on 1st page: Priya V.", type: "text", required: false }
        ],
        "CLOTHING_BAGS": [
            { key: "itemType", label: "Item Type", placeholder: "e.g. Backpack, Denim Jacket, RayBan Sunglasses", type: "text", required: true },
            { key: "brand", label: "Brand Name", placeholder: "e.g. Nike, Skybags, Fastrack", type: "text", required: true },
            { key: "color", label: "Primary Color", placeholder: "e.g. Navy Blue with Yellow Zip", type: "text", required: true },
            { key: "sizePattern", label: "Size or Pattern Details", placeholder: "e.g. Size M / Striped design", type: "text", required: false }
        ],
        "OTHER": [
            { key: "itemDescription", label: "Detailed Item Description", placeholder: "e.g. Stainless Steel Water Bottle with stickers", type: "text", required: true },
            { key: "color", label: "Color / Markings", placeholder: "e.g. Silver bottle with scratch on top", type: "text", required: true }
        ]
    };

    function renderQuestions(categoryKey, prefilledData = {}) {
        if (!questionsContainer) return;
        questionsContainer.innerHTML = "";

        const fields = questionSchemas[categoryKey] || questionSchemas["OTHER"];

        const headerDiv = document.createElement("div");
        headerDiv.className = "mb-4 border-b border-gray-200 pb-2";
        headerDiv.innerHTML = `<h3 class="text-sm font-semibold text-blue-800 uppercase tracking-wider">Category-Specific Unique Questions</h3>
            <p class="text-xs text-gray-500">Provide exact details to ensure accurate AI automated matching.</p>`;
        questionsContainer.appendChild(headerDiv);

        fields.forEach(field => {
            const fieldGroup = document.createElement("div");
            fieldGroup.className = "mb-4";

            const val = prefilledData[field.key] || "";
            const isPrefilled = prefilledData[field.key] ? "bg-purple-50 border-purple-300 font-medium" : "";

            fieldGroup.innerHTML = `
                <label class="block text-xs font-bold text-gray-700 uppercase mb-1">${field.label} ${field.required ? '<span class="text-red-500">*</span>' : ''}</label>
                <input type="${field.type}" 
                       name="attr_${field.key}" 
                       id="attr_${field.key}"
                       value="${escapeHtml(val)}"
                       placeholder="${field.placeholder}" 
                       class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition duration-150 ${isPrefilled}" 
                       ${field.required ? 'required' : ''} />
            `;
            questionsContainer.appendChild(fieldGroup);
        });
    }

    // Handle Category change manually
    if (categorySelect) {
        categorySelect.addEventListener("change", function () {
            renderQuestions(this.value);
        });
        // Initial render
        if (categorySelect.value) {
            renderQuestions(categorySelect.value);
        }
    }

    // Image Upload & AI Vision Recognition Trigger
    if (imageInput) {
        imageInput.addEventListener("change", function (e) {
            const file = e.target.files[0];
            if (!file) return;

            // Image Preview
            if (imagePreview) {
                const reader = new FileReader();
                reader.onload = function (event) {
                    imagePreview.src = event.target.result;
                    imagePreview.classList.remove("hidden");
                };
                reader.readAsDataURL(file);
            }

            // Trigger AI Vision Recognition
            if (aiStatusBanner) {
                aiStatusBanner.innerHTML = `
                    <div class="flex items-center space-x-3 p-3 bg-purple-50 border border-purple-200 rounded-lg text-purple-800 text-sm ai-pulse">
                        <svg class="animate-spin h-5 w-5 text-purple-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        <span><strong>AI Vision Analyzer Running:</strong> Recognizing image features and auto-answering questions...</span>
                    </div>
                `;
                aiStatusBanner.classList.remove("hidden");
            }

            const formData = new FormData();
            formData.append("image", file);

            // Send to REST API endpoint
            fetch("/api/analyze-image", {
                method: "POST",
                body: formData
            })
            .then(res => res.json())
            .then(data => {
                if (data && data.success) {
                    // Auto select category
                    if (categorySelect && data.detectedCategory) {
                        categorySelect.value = data.detectedCategory;
                    }

                    // Auto populate title & description if empty
                    const titleInput = document.getElementById("title");
                    if (titleInput && (!titleInput.value || titleInput.value.trim() === "")) {
                        titleInput.value = data.suggestedTitle || "";
                    }

                    const descInput = document.getElementById("description");
                    if (descInput && (!descInput.value || descInput.value.trim() === "")) {
                        descInput.value = data.suggestedDescription || "";
                    }

                    // Render questions prefilled with AI extractions
                    renderQuestions(data.detectedCategory || categorySelect.value, data.extractedAttributes || {});

                    if (aiStatusBanner) {
                        aiStatusBanner.innerHTML = `
                            <div class="flex items-center justify-between p-3 bg-green-50 border border-green-200 rounded-lg text-green-800 text-sm">
                                <div class="flex items-center space-x-2">
                                    <svg class="w-5 h-5 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"></path>
                                    </svg>
                                    <span><strong>AI Recognition Complete!</strong> Object detected as <strong>${data.detectedCategory}</strong> (${Math.round(data.confidenceScore * 100)}% confidence). Questions pre-filled.</span>
                                </div>
                                <span class="text-xs bg-green-200 text-green-900 font-semibold px-2 py-0.5 rounded">Auto-Answered</span>
                            </div>
                        `;
                    }
                }
            })
            .catch(err => {
                if (aiStatusBanner) {
                    aiStatusBanner.innerHTML = `
                        <div class="p-3 bg-yellow-50 border border-yellow-200 rounded-lg text-yellow-800 text-sm">
                            Image uploaded. You can manually fill the question fields below.
                        </div>
                    `;
                }
            });
        });
    }

    function escapeHtml(str) {
        if (!str) return '';
        return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
    }
});
