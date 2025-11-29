from django.urls import path
from .views import *

urlpatterns = [
    path('user-control/register', Register.as_view()),
    path('user-control/verification/<str:token>', Verification.as_view()),
    path('user-control/login', Login.as_view()),
]