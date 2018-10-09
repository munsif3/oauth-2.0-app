var selectedFileCount, totalUploadedValue, fileCount, filesUploaded;

// When upload is completed
function onUploadComplete(e) {
	totalUploadedValue += document.getElementById('files').files[filesUploaded].size;
	filesUploaded++;
	if (filesUploaded < fileCount) {
		uploadNext();
	} else {
		var bar = document.getElementById('bar');
		bar.style.width = '100%';
		bar.innerHTML = '100% complete';
		swal("Success!", "Finished uploading file(s)", "success");
	}
}

// Will be called when user select the files
function onFileSelect(e) {
	files = e.target.files; // FileList object
	var output = [];
	fileCount = files.length;
	selectedFileCount = 0;
	for (var i = 0; i < fileCount; i++) {
		var file = files[i];
		output.push(file.name, ' (', file.size, ' bytes, ', file.lastModifiedDate
				.toLocaleDateString(), ')');
		output.push('<br/>');
		selectedFileCount += file.size;
	}
	document.getElementById('selectedFiles').innerHTML = output.join('');

}

// Update the progress bar
function onUploadProgress(e) {
	if (e.lengthComputable) {
		var percentComplete = parseInt((e.loaded + totalUploadedValue) * 100 / selectedFileCount);
		var bar = document.getElementById('bar');
		bar.style.width = percentComplete + '%';
		bar.innerHTML = percentComplete + ' % Completed';
	} else {
		console.err("Unable to compute length");
	}
}

// Capture errors
function onUploadFailed(e) {
	swal("Error!", "Error uploading file(s)", "danger");
}

// Get the next file in the queue and send it to the server
function uploadNext() {
	var xhr = new XMLHttpRequest();
	var fd = new FormData();
	var file = document.getElementById('files').files[filesUploaded];
	fd.append("multipartFile", file);
	xhr.upload.addEventListener("progress", onUploadProgress, false);
	xhr.addEventListener("load", onUploadComplete, false);
	xhr.addEventListener("error", onUploadFailed, false);
	xhr.open("POST", "/upload");
	xhr.send(fd);
}

// Start the process
function startUpload() {
	if (document.getElementById('files').files.length <= 0) {
		swal("Cannot Upload!", "Please select file(s) to upload", "warning");
	} else {
		totalUploadedValue = filesUploaded = 0;
		uploadNext();
	}
}

// Clear the page
function resetScreen() {
	document.getElementById('bar').style.width = '0%';
	document.getElementById('bar').innerText = '';
	document.getElementById("selectedFiles").innerHTML = '';
	document.getElementById("imageForm").reset();
}

// Event listeners for button clicks
window.onload = function() {
	document.getElementById('files').addEventListener('change', onFileSelect, false);
	document.getElementById('uploadButton').addEventListener('click', startUpload, false);
	document.getElementById('resetButton').addEventListener('click', resetScreen, false);
}
