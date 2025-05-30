			const video = document.getElementById('camera-preview');
			var captureButton = document.getElementById('Takephoto');
			const recaptureButton = document.getElementById('RecaptureButton');
			recaptureButton.style.display = 'none';
			const SaveButton = document.getElementById('SaveButton');
			SaveButton.style.display = 'none';
			const capturedImage = document.getElementById('capturedImage').getContext('2d');
			const imageContainer = document.getElementById('imageContainer');
			const capturedImageDataInput = document.getElementById('image');
			const StartCaptureButton = document.getElementById('StartCapture');
			const Videcontainer = document.getElementById('videoContainer');
			let mediaStream = null;

			function generateRandomString() {
				return Math.random().toString(36).substring(2, 15);
			}

			function startCapture() {
				videoContainer = document.getElementById('videoContainer');
				videoContainer.style.display = 'block';
				if (StartCaptureButton != null) {
					StartCaptureButton.style.display = 'none';
				}
				cardimage = document.getElementById('cardimg');
				cardimage.style.display = 'none';
				captureButton.style.display = 'block';
				startCamera();
			}


			function startCamera() {
				navigator.mediaDevices.getUserMedia({video: true})
					.then((stream) => {
						video.srcObject = stream;
						mediaStream = stream;
					})
					.catch((error) => {
						console.error('Error accessing camera:', error);
					});
			}

			captureButton.addEventListener('click', () => {
				capturedImage.drawImage(video, 0, 0, 200, 200);
				const capturedImageData = capturedImage.canvas.toDataURL('image/png');
				const capturedImageElement = document.getElementById('cardimg');
				capturedImageElement.style.display = 'block';
				captureButton.style.display = 'none';
				Videcontainer.style.display = 'none';
				capturedImageElement.src = capturedImageData;
				recaptureButton.style.display = 'block';
				SaveButton.style.display = 'block';
				const randomFilename = 'captured-image-' + generateRandomString() + '.png';
				capturedImage.canvas.toBlob(function (blob) {
					capturedFile = new File([blob], randomFilename, {type: 'image/png'});
				}, 'image/png');
			});


			recaptureButton.addEventListener('click', () => {
				recaptureButton.style.display = 'none';
				SaveButton.style.display = 'none';
				videoContainer.style.display = 'block';
				captureButton.style.display = 'block';
				const Imagediv = document.getElementById('Imagediv');
				var imgdel=Imagediv.querySelector('#cardimg');
				imgdel.style.display='none';
				video.srcObject = null;
				startCamera();
			});
			
			function uploadImage() {
				console.log("Inside Upload Image");
				if (!capturedFile) {
					console.error('No image captured');
					return;
				}
				console.log("below capture file")

				var formData = new FormData();
				formData.append('file', capturedFile);

				// Using Fetch API to send the image to the server
				fetch('/Visitorsave', {
					method: 'POST',
					body: formData
				})
					.then(response => {
						if (response.ok) {
							console.log('Image uploaded successfully');
						} else {
							console.error('Image upload failed');
						}
					})
					.catch(error => {
						console.error('Error:', error);
					});

				window.location.href = '/visitor';
			}