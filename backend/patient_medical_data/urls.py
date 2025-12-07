from rest_framework.routers import DefaultRouter
from .views import PatientMedicalDataViewSet 

router = DefaultRouter()

router.register(r'medical-data', PatientMedicalDataViewSet) 

urlpatterns = router.urls