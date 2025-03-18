@import java.util.Map
@import java.util.Optional

@param Optional<String> error = Optional.empty()

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Admin Panel</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --primary-bg: #ffffff;
            --secondary-bg: #f8f9fa;
            --primary-text: #212529;
            --secondary-text: #6c757d;
            --accent: #000000;
            --border: #dee2e6;
            --hover: #e9ecef;
            --error: #dc3545;
            --success: #28a745;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
        }

        body {
            background-color: var(--secondary-bg);
            color: var(--primary-text);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .login-container {
            width: 100%;
            max-width: 420px;
            padding: 40px;
        }

        .login-card {
            background-color: var(--primary-bg);
            border-radius: 12px;
            box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
            overflow: hidden;
        }

        .login-header {
            padding: 40px 32px 20px;
            text-align: center;
        }

        .logo {
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 16px;
        }

        .login-subtitle {
            font-size: 16px;
            color: var(--secondary-text);
            margin-bottom: 30px;
        }

        .login-form {
            padding: 0 32px 40px;
        }

        .form-group {
            margin-bottom: 24px;
        }

        .form-label {
            display: block;
            margin-bottom: 8px;
            font-size: 14px;
            font-weight: 500;
        }

        .form-control {
            width: 100%;
            padding: 12px 16px;
            font-size: 16px;
            border: 1px solid var(--border);
            border-radius: 8px;
            transition: border-color 0.2s, box-shadow 0.2s;
        }

        .form-control:focus {
            outline: none;
            border-color: var(--accent);
            box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.08);
        }

        .input-group {
            position: relative;
        }

        .input-group .form-control {
            padding-right: 48px;
        }

        .input-group-append {
            position: absolute;
            top: 50%;
            right: 16px;
            transform: translateY(-50%);
            cursor: pointer;
            color: var(--secondary-text);
        }

        .remember-me {
            display: flex;
            align-items: center;
            margin-bottom: 24px;
        }

        .remember-me input {
            margin-right: 8px;
        }

        .btn {
            display: block;
            width: 100%;
            padding: 14px;
            background-color: var(--accent);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: opacity 0.2s;
        }

        .btn:hover {
            opacity: 0.9;
        }

        .login-footer {
            margin-top: 24px;
            text-align: center;
            font-size: 14px;
            color: var(--secondary-text);
        }

        .login-footer a {
            color: var(--accent);
            text-decoration: none;
            font-weight: 500;
        }

        .login-footer a:hover {
            text-decoration: underline;
        }

        .error-message {
            background-color: rgba(220, 53, 69, 0.1);
            color: var(--error);
            padding: 12px 16px;
            border-radius: 8px;
            margin-bottom: 24px;
            font-size: 14px;
            display: flex;
            align-items: center;
        }

        .error-message i {
            margin-right: 8px;
            font-size: 16px;
        }

        @media (max-width: 480px) {
            .login-container {
                padding: 20px;
            }

            .login-header {
                padding: 30px 24px 15px;
            }

            .login-form {
                padding: 0 24px 30px;
            }
        }
    </style>
</head>
<body>

<div class="login-container">
    <div class="login-card">
        <div class="login-header">
            <h1 class="logo">AdminPanel</h1>
            <p class="login-subtitle">Sign in to your account</p>
        </div>
        <div class="login-form">
            @if(error.isPresent())
                <div class="error-message">
                    <i class="fas fa-exclamation-circle"></i>
                    @if(error.get().equals("invalid_credentials"))
                        Invalid email or password. Please try again.
                    @elseif(error.get().equals("request_unauthorized"))
                        Request Unauthorized. Please login to continue.
                    @else
                        An error occurred. Please try again.
                    @endif
                </div>
            @endif

            <form id="loginForm" onsubmit="return false;">
                <div class="form-group">
                    <label for="email" class="form-label">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" placeholder="Enter your email" required autofocus>
                </div>
                <div class="form-group">
                    <label for="password" class="form-label">Password</label>
                    <div class="input-group">
                        <input type="password" id="password" name="password" class="form-control" placeholder="Enter your password" required>
                        <div class="input-group-append" id="togglePassword">
                            <i class="far fa-eye"></i>
                        </div>
                    </div>
                </div>
                <div class="remember-me">
                    <input type="checkbox" id="remember" name="remember">
                    <label for="remember">Remember me</label>
                </div>
                <button type="submit" class="btn" id="submitBtn">Sign In</button>
            </form>

            <div class="login-footer">
                <p>Forgot your password? <a href="/reset-password">Reset it here</a></p>
            </div>
        </div>
    </div>
</div>

<script>
    @raw
    // Toggle password visibility
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');

    togglePassword.addEventListener('click', function() {
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);

        // Toggle eye icon
        this.querySelector('i').classList.toggle('fa-eye');
        this.querySelector('i').classList.toggle('fa-eye-slash');
    });

    // Handle form submission with JSON
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        // Create JSON payload
        const payload = {
            email: email,
            password: password
        };

        // Send data to server
        fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        })
            .then(response => {
                if (!response.ok) {
                    // Redirect with error parameter if authentication fails
                    window.location.href = window.location.pathname + '?error=invalid_credentials';
                    return;
                }
                return response.json();
            })
            .then(data => {
                if (data && data.token) {
                    // Store token and redirect to dashboard
                    localStorage.setItem('authToken', data.token);
                    window.location.href = '/';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                window.location.href = window.location.pathname + '?error=invalid_credentials';
            });
    });
    @endraw
</script>
</body>
</html>