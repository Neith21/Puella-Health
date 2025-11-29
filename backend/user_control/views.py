from rest_framework.views import APIView
from django.http import JsonResponse, Http404, HttpResponseRedirect
from http import HTTPStatus
from django.contrib.auth.models import User #Django´s Model
import uuid
import os
from django.contrib.auth import authenticate
from django.core.exceptions import ValidationError
import re
from urllib.parse import urlencode
from django.utils import timezone
from django.db.models import Q

#JWT
from jose import jwt
from django.conf import settings
from datetime import datetime, timedelta
import time


from .models import *
from utilities.utilities import sendMail
from django.db import transaction
from django.core.validators import validate_email


# Create your views here.


def get_base_url():
    base_url = os.getenv("BASE_URL", "http://127.0.0.1:8000")
    port = os.getenv("BASE_URL_BACKEND_PORT")
    if port:
        return f"{base_url}:{port}"
    return base_url

def get_frontend_base_url():
    base_url = os.getenv("BASE_URL", "http://localhost:5173/autorent-leon/")
    port = os.getenv("BASE_URL_FRONTEND_PORT")
    if port:
        return f"{base_url}:{port}"
    return base_url


def validate_required_fields(data, fields):
    for field in fields:
        value = data.get(field)
        if value is None:
            return JsonResponse({
                "status": "error",
                "message": f"El campo '{field}' es requerido."
            }, status=HTTPStatus.BAD_REQUEST)
        if isinstance(value, str) and not value.strip():
            return JsonResponse({
                "status": "error",
                "message": f"El campo '{field}' no puede estar vacío."
            }, status=HTTPStatus.BAD_REQUEST)
    return None


def validate_password_complexity(password):
    if len(password) < 8:
        return "La contraseña debe tener al menos 8 caracteres."
    if not re.search(r"[A-Z]", password):
        return "La contraseña debe contener al menos una letra mayúscula."
    if not re.search(r"[a-z]", password):
        return "La contraseña debe contener al menos una letra minúscula."
    if not re.search(r"\d", password):
        return "La contraseña debe contener al menos un número."
    if not re.search(r"[!@#$%^&*(),.?\":{}|<>]", password):
         return "La contraseña debe contener al menos un carácter especial."
    return None


def validate_name_format(field, field_name):
    if not re.match(r"^[a-zA-ZÀ-ÿ\s'-]+$", field):
        return f"El campo '{field_name}' contiene caracteres no válidos. Solo se permiten letras y espacios."
    return None


class Register(APIView):


    def post(self, request):

        # Validate required fields
        required_fields = ["name", "email", "password"]
        error_response = validate_required_fields(request.data, required_fields)
        if error_response:
            return error_response
        
        first_name = request.data.get("name").strip()
        email = request.data.get("email").strip().lower()
        password = request.data.get("password")

        name_error = validate_name_format(first_name, "nombre")
        if name_error:
            return JsonResponse({"status": "error", "message": name_error}, status=HTTPStatus.BAD_REQUEST)

        try:
            validate_email(email)
        except ValidationError:
            return JsonResponse({
                "status": "error",
                "message": "El formato del correo electrónico no es válido."
            }, status=HTTPStatus.BAD_REQUEST)
        
        password_error = validate_password_complexity(password)
        if password_error:
            return JsonResponse({"status": "error", "message": password_error}, status=HTTPStatus.BAD_REQUEST)

        if User.objects.filter(email=email).exists():
            return JsonResponse({
                "status": "error",
                "message": f"El correo electrónico '{email}' ya está registrado."
            }, status=HTTPStatus.CONFLICT)

        token = uuid.uuid4()
        base_url = get_base_url()
        url = f"{base_url}/api/v1/user-control/verification/{token}"

        try:
            # Uso de atomic para crear una transacción
            with transaction.atomic():
                user = User.objects.create_user(
                    username=email,
                    password=password,
                    email=email,
                    first_name=first_name,
                    last_name="",
                    is_staff=False,
                    is_active=0
                )

                # CAMBIO REALIZADO PARA FUNCIONAMIENTO DE LA BITACORA
                metadata = UsersMetadata.objects.create(token=token, user_id=user.id)
                history_record = metadata.history.last()
                history_record.history_user = user
                history_record.history_change_reason = "Creación de usuario, esperando verificación"
                history_record.save()

                html = f"""
                    <div style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 30px;">
                        <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                            <h2 style="color: #333;">¡Verificación de cuenta!</h2>
                            <p style="font-size: 16px; color: #555;">
                                Hola <strong>{first_name}</strong>,
                            </p>
                            <p style="font-size: 16px; color: #555;">
                                Te has registrado exitosamente. Para activar tu cuenta, por favor haz clic en el siguiente botón:
                            </p>
                            <p style="text-align: center; margin: 30px 0;">
                                <a href="{url}" style="background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-size: 16px;">
                                    Activar cuenta
                                </a>
                            </p>
                            <p style="font-size: 14px; color: #777;">
                                Si el botón anterior no funciona, copia y pega el siguiente enlace en tu navegador:
                            </p>
                            <p style="font-size: 14px; word-break: break-all; color: #555;">{url}</p>
                            <hr style="margin: 40px 0;">
                            <p style="font-size: 12px; color: #aaa; text-align: center;">
                                Si no solicitaste esta cuenta, puedes ignorar este mensaje.
                            </p>
                        </div>
                    </div>
                """

                # Si falla el envío de correo, se lanzará una excepción y se revertirá la transacción
                sendMail(
                    html_content=html,
                    subject="Verificación de Cuenta",
                    recipient_email=email
                )

            # Si llegamos aquí, es porque todo se completó correctamente
            return JsonResponse({
                "status": "ok",
                "message": "Usuario creado exitosamente.",
                "user_id": user.id
            }, status=HTTPStatus.CREATED)

        except Exception as e:
            return JsonResponse({
                "status": "error",
                "message": f"No se pudo guardar el usuario. Por favor, inténtelo de nuevo más tarde. {e}"
            }, status=HTTPStatus.INTERNAL_SERVER_ERROR)


class Verification(APIView):


    def get(self, request, token):
        frontend_base_url = get_frontend_base_url()
        login_path = "/autorent-leon/#/login"
        register_path = "/autorent-leon/#/register"

        messages_es = {
            "TOKEN_REQUIRED": "Token no proporcionado o inválido.",
            "ALREADY_ACTIVE": "Tu cuenta ya ha sido activada. Por favor, inicia sesión.",
            "VERIFICATION_EXPIRED": "Tu tiempo de verificación ha expirado (más de 24 horas). Debes registrarte nuevamente.",
            "VERIFICATION_SUCCESS": "¡Tu cuenta ha sido activada exitosamente! Ahora puedes iniciar sesión.",
            "INVALID_TOKEN_OR_USED": "El enlace de verificación no es válido, ya ha sido utilizado o la cuenta no existe. Intenta iniciar sesión o regístrate.",
            "USER_CORRUPT": "Error de datos: El usuario asociado al token no existe. Por favor, regístrate de nuevo.",
            "GENERAL_ERROR": "Ocurrió un error inesperado durante la verificación. Intenta más tarde o contacta a soporte."
        }

        if not token or token.strip() == "":
            params = urlencode({'status': 'error', 'message_key': 'TOKEN_REQUIRED'})
            return HttpResponseRedirect(f"{frontend_base_url}{register_path}?{params}")

        try:
            metadata = UsersMetadata.objects.get(token=token)
            try:
                user_account = metadata.user
                if user_account is None:
                    raise User.DoesNotExist("Usuario no encontrado")
            except (User.DoesNotExist, AttributeError):
                params = urlencode({'status': 'error', 'message_key': 'USER_CORRUPT'})
                return HttpResponseRedirect(f"{frontend_base_url}{register_path}?{params}")

            if user_account.is_active:
                with transaction.atomic():
                    if metadata.token:
                        metadata.token = ""
                        metadata.save(update_fields=['token'])
                params = urlencode({'status': 'info', 'message_key': 'ALREADY_ACTIVE'})
                return HttpResponseRedirect(f"{frontend_base_url}{login_path}?{params}")

            # Verificar si ha expirado (más de 24 horas)
            time_since_creation = timezone.now() - user_account.date_joined
            if time_since_creation > timedelta(days=1):
                with transaction.atomic():
                    metadata.delete()
                    user_account.delete()
                params = urlencode({'status': 'error', 'message_key': 'VERIFICATION_EXPIRED'})
                return HttpResponseRedirect(f"{frontend_base_url}{register_path}?{params}")
            
            # Activar el usuario
            else:
                with transaction.atomic():
                    user_account.is_active = True
                    user_account.save(update_fields=['is_active'])
                    if metadata.token:
                        metadata.token = ""
                        metadata.save(update_fields=['token'])
                params = urlencode({'status': 'success', 'message_key': 'VERIFICATION_SUCCESS'})
                return HttpResponseRedirect(f"{frontend_base_url}{login_path}?{params}")

        except UsersMetadata.DoesNotExist:
            params = urlencode({'status': 'error', 'message_key': 'INVALID_TOKEN_OR_USED'})
            return HttpResponseRedirect(f"{frontend_base_url}{login_path}?{params}")
        
        except Exception as e:
            import logging
            logger = logging.getLogger(__name__)
            logger.error(f"Error en verificación de token {token}: {str(e)}")
            
            params = urlencode({'status': 'error', 'message_key': 'GENERAL_ERROR'})
            return HttpResponseRedirect(f"{frontend_base_url}{login_path}?{params}")


class Login(APIView):


    def post(self, request):


        required_fields = ["email", "password"]
        error_response = validate_required_fields(request.data, required_fields)
        if error_response:
            return error_response

        email_or_username = request.data.get("email").strip()
        password = request.data.get("password")

        user_obj = None
        try:
            user_obj = User.objects.get(Q(username=email_or_username) | Q(email=email_or_username.lower()))
        except User.DoesNotExist:
            return JsonResponse({
                "status": "error",
                "message": "Credenciales inválidas o usuario no encontrado."
            }, status=HTTPStatus.UNAUTHORIZED)

        user = authenticate(request, username=user_obj.username, password=password)

        if user is not None:
            user.last_login = timezone.now()
            user.save(update_fields=['last_login'])

            now = datetime.now()
            expiration = now + timedelta(days=1)
            expiration_timestamp = int(datetime.timestamp(expiration))
            
            base_url_with_port = get_base_url()

            payload = {
                "id": user.id,
                "username": user.username,
                "email": user.email,
                "first_name": user.first_name,
                "last_name": user.last_name,
                "is_superuser": user.is_superuser,
                "iss": base_url_with_port,
                "iat": int(time.time()),
                "exp": expiration_timestamp
            }

            try:
                token = jwt.encode(payload, settings.SECRET_KEY, algorithm='HS512')
                return JsonResponse({
                    "status": "ok",
                    "message": "Inicio de sesión exitoso.",
                    "token": token,
                    "user": {
                        "id": user.id,
                        "username": user.username,
                        "email": user.email,
                        "first_name": user.first_name
                    }
                })
            except Exception as e:
                return JsonResponse({
                    "status": "error",
                    "message": f"No se pudo generar el token de autenticación. {e}"
                }, status=HTTPStatus.INTERNAL_SERVER_ERROR)
        else:
            return JsonResponse({
                "status": "error",
                "message": "Credenciales inválidas o usuario no encontrado."
            }, status=HTTPStatus.UNAUTHORIZED)