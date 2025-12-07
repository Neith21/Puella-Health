from rest_framework.routers import DefaultRouter
from .views import HealthRecordViewSet

router = DefaultRouter()

router.register(r'health-records', HealthRecordViewSet, basename='health_record')

urlpatterns = router.urls