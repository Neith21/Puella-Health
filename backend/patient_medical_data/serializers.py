from rest_framework import serializers
from .models import PatientMedicalData

class PatientMedicalDataSerializer(serializers.ModelSerializer):
    class Meta:
        model = PatientMedicalData
        fields = '__all__'
        read_only_fields = (
            'created_by', 
            'modified_by', 
            'created_at', 
            'updated_at',
            'active'
        )