from rest_framework.routers import DefaultRouter
from .views import HealthRecordViewSet

router = DefaultRouter()

router.register(r'health-records', HealthRecordViewSet)

urlpatterns = router.urls